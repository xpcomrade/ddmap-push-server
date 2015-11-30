package com.ddmap.push.test.mock.sendmsg;

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

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

/**
 * @Title: Client.java
 * @Description: 测试Server和Client互发消息，及重连
 * @date 2012-2-28 上午09:07:59
 * @version V1.0
 */
public class Client {
	private static TestAsyncCallback testAsyncCallback = new TestAsyncCallback();
	private static ServerService serverService;

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		HsfConnector connector = new HsfConnectorImpl();
		connector.getListeners().add(new ConnectorChannelEventHandler());
		connector.registerService(new ClientServiceImpl());
		serverService = ServiceProxyFactory.getRoundFactoryInstance(connector).wrapSyncProxy(ServerService.class);

		connector.connect(new InetSocketAddress("127.0.0.1", 8082));

		// final ServerService serverService =
		// ServiceProxyFactory.getRoundFactoryInstance(connector).wrapAsyncCallbackProxy(
		// ServerService.class, testAsyncCallback);
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
