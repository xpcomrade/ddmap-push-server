package com.ddmap.push.test.inversion;

import org.jboss.netty.channel.ChannelHandlerContext;
import com.ddmap.push.netty.channel.HsfChannel;
import com.ddmap.push.netty.listener.ChannelEventListenerAdapter;
import com.ddmap.push.netty.listener.EventBehavior;
import com.ddmap.push.netty.service.HsfAcceptor;
import com.ddmap.push.netty.service.HsfAcceptorImpl;
import com.ddmap.push.proxy.ServiceProxyFactory;
import com.ddmap.push.test.service.TestService;
import com.ddmap.push.test.service.TestServiceImpl;

/**
 * @Title: Server.java
 * @Description: TODO(添加描述)
 * @date 2012-2-23 上午12:58:53
 * @version V1.0
 */
public class Server {
	public static void main(String[] args) {
		HsfAcceptor acceptor = new HsfAcceptorImpl();
		acceptor.getListeners().add(new ChannelEventListenerAdapter() {
			@Override
			public EventBehavior groupCreated(ChannelHandlerContext ctx, HsfChannel channel, String groupName) {

				final TestService testService = ServiceProxyFactory.getRoundFactoryInstance(channel.getService()).wrapSyncProxy(
						TestService.class);

				try {
					testService.test("大家都有过复制一个大文件时，久久等待却不见结束，明明很着急却不能取消的情况吧——一旦取消，一切都要从头开始！");
				} catch (Exception e) {
					e.printStackTrace();
				}

				return super.groupCreated(ctx, channel, groupName);
			}
		});

		acceptor.bind(8082);
	}
}
