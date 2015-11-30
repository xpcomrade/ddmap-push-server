package com.ddmap.push.test.configserver;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.channel.ChannelHandlerContext;
import com.ddmap.push.configserver.ConfigConsumer;
import com.ddmap.push.configserver.ConfigConsumerImpl;
import com.ddmap.push.netty.channel.HsfChannel;
import com.ddmap.push.netty.listener.ChannelEventListenerAdapter;
import com.ddmap.push.netty.listener.EventBehavior;
import com.ddmap.push.netty.service.HsfConnector;
import com.ddmap.push.netty.service.HsfConnectorImpl;
import com.ddmap.push.proxy.ServiceProxyFactory;
import com.ddmap.push.test.service.TestService;
import com.ddmap.push.util.AddressUtil;
import com.ddmap.push.util.HsfOptions;

/**
 * 配置服务使用者
 * 
 * @Title: CSConsumer.java
 * @Package com.ddmap.push.test.configserver
 * @date 2012-3-22 下午10:12:13
 * @version V1.0
 */
public class CSConsumer {
	private static AtomicLong seq = new AtomicLong();
	private static TestService testService;
	private static ConfigConsumer consumer;

	public static void main(String[] args) {
		HsfConnector connector = new HsfConnectorImpl();
		connector.getListeners().add(new ConnectorChannelListener());
		testService = ServiceProxyFactory.getRoundFactoryInstance(connector).wrapSyncProxy(TestService.class);
		//
		consumer = new ConfigConsumerImpl();
		consumer.setOption(HsfOptions.SYNC_INVOKE_TIMEOUT, 600000);
		consumer.setOption(HsfOptions.HANDSHAKE_TIMEOUT, 600000);
		consumer.setConnector(connector);
		consumer.subscribe("TestService");
		consumer.connect(AddressUtil.parseAddress("127.0.0.1:8082"));

		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				if (seq.incrementAndGet() % 2 == 1) {
					consumer.unsubscribe("TestService");
				} else {
					consumer.subscribe("TestService");
				}
			}
		}, 1000L, 5000L, TimeUnit.MILLISECONDS);
	}

	private static class ConnectorChannelListener extends ChannelEventListenerAdapter {
		@Override
		public EventBehavior groupCreated(ChannelHandlerContext ctx, HsfChannel channel, String groupName) {
			System.out.println(testService.test("i'm config consumer"));
			return super.groupCreated(ctx, channel, groupName);
		}
	}
}
