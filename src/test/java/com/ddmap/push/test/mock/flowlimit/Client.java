package com.ddmap.push.test.mock.flowlimit;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

import org.jboss.netty.channel.ChannelHandlerContext;
import com.ddmap.push.netty.channel.HsfChannel;
import com.ddmap.push.netty.listener.ChannelEventListenerAdapter;
import com.ddmap.push.netty.listener.EventBehavior;
import com.ddmap.push.netty.service.HsfConnector;
import com.ddmap.push.netty.service.HsfConnectorImpl;
import com.ddmap.push.proxy.ServiceProxyFactory;
import com.ddmap.push.test.mock.service.ClientServiceImpl;
import com.ddmap.push.test.mock.service.ServerService;
import com.ddmap.push.util.AsyncCallback;
import com.ddmap.push.util.HsfOptions;

public class Client {
	private static TestAsyncCallback testAsyncCallback = new TestAsyncCallback();
	private static ServerService serverService;

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		HsfConnector connector = new HsfConnectorImpl();
		connector.setOption(HsfOptions.FLOW_LIMIT, 1);
		connector.getListeners().add(new ConnectorChannelEventHandler());
		connector.registerService(new ClientServiceImpl());
		serverService = ServiceProxyFactory.getRoundFactoryInstance(connector).wrapSyncProxy(ServerService.class);

		connector.connect(new InetSocketAddress("192.168.1.26", 8082));

		for (int i = 0; i < 100000; i++) {
			System.out.println(serverService.callServer(i + "/" + connector.getFlowManager().getAvailable()));
		}
	}

	public static class TestAsyncCallback extends AsyncCallback<Object> {
		public void doCallback(Object data) {
			System.out.println(data);
		};
	}

	static class ConnectorChannelEventHandler extends ChannelEventListenerAdapter {
		@Override
		public EventBehavior groupCreated(ChannelHandlerContext ctx, HsfChannel channel, String groupName) {
			//
			for (int i = 0; i < 10; i++) {
				System.out.println(serverService.callServer(" client test " + i));
			}

			return EventBehavior.Continue;
		}
	}
}
