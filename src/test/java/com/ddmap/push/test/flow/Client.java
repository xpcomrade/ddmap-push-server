package com.ddmap.push.test.flow;

import com.ddmap.push.netty.channel.HsfChannel;
import com.ddmap.push.netty.service.HsfConnector;
import com.ddmap.push.netty.service.HsfConnectorImpl;
import com.ddmap.push.proxy.ServiceProxyFactory;
import com.ddmap.push.test.service.TestService;
import com.ddmap.push.util.AsyncCallback;
import com.ddmap.push.util.HsfOptions;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Title: Client.java
 * @Description: TODO(添加描述)
 * @date 2012-2-23 上午01:01:33
 * @version V1.0
 */
public class Client {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		HsfConnector connector = new HsfConnectorImpl();
		connector.setOption(HsfOptions.FLOW_LIMIT, 100);
		connector.connect(new InetSocketAddress("127.0.0.1", 8082));

		final AtomicInteger num = new AtomicInteger();
		final TestService testService = ServiceProxyFactory.getRoundFactoryInstance(connector).wrapAsyncCallbackProxy(
				TestService.class, new AsyncCallback<String>() {
					@Override
					public void doCallback(String data) {
						System.out.println("received:" +  data);
						//
						num.incrementAndGet();
					}
					
					@Override
					public void doExceptionCaught(Throwable ex, HsfChannel channel, Object param) {
						super.doExceptionCaught(ex, channel, param);
					}
				});

		for (int i = 0; i < 100; i++) {
			try {
				testService.test("大家都有过复制一个大文件时，久久等待却不见结束，明明很着急却不能取消的情况吧——一旦取消，一切都要从头开始！");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println(connector.getFlowManager().getAvailable());
		connector.shutdown();
		
		Thread.sleep(2000L);
		System.out.println(connector.getFlowManager().getAvailable());
	}

}
