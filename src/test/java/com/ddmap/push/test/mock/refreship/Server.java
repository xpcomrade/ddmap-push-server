package com.ddmap.push.test.mock.refreship;

import org.jboss.netty.channel.ChannelHandlerContext;
import com.ddmap.push.netty.channel.HsfChannel;
import com.ddmap.push.netty.listener.ChannelEventListenerAdapter;
import com.ddmap.push.netty.listener.EventBehavior;
import com.ddmap.push.netty.service.HsfAcceptor;
import com.ddmap.push.netty.service.HsfAcceptorImpl;
import com.ddmap.push.proxy.ServiceProxyFactory;
import com.ddmap.push.test.mock.service.ClientService;
import com.ddmap.push.test.mock.service.ServerServiceImpl;

/**
 * @Title: Server.java
 * @Package com.ddmap.push.future
 * @Description: TODO(添加描述)
 * @date 2012-2-28 上午09:07:30
 * @version V1.0
 */
public class Server {
	private static ClientService clientService;

	public static void main(String[] args) {
		HsfAcceptor acceptor = new HsfAcceptorImpl();
		acceptor.getListeners().add(new AcceptorChannelEventHandler());
		acceptor.registerService(new ServerServiceImpl());
		//
		clientService = ServiceProxyFactory.getRoundFactoryInstance(acceptor).wrapSyncProxy(ClientService.class);
		//
		acceptor.bind(8082);
	}

	static class AcceptorChannelEventHandler extends ChannelEventListenerAdapter {
		@Override
		public EventBehavior groupCreated(ChannelHandlerContext ctx, HsfChannel channel, String groupName) {
			//
			for (int i = 0; i < 10; i++) {
				System.out.println(clientService.callClient(" server test " + i));
			}

			return EventBehavior.Continue;
		}
	}
}
