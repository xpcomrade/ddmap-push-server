package com.ddmap.push.test.future;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.ddmap.push.future.ChannelGroupFuture;
import com.ddmap.push.future.InvokeFuture;
import com.ddmap.push.netty.channel.HsfChannelGroup;
import com.ddmap.push.netty.service.HsfConnector;
import com.ddmap.push.netty.service.HsfConnectorImpl;
import com.ddmap.push.proxy.ServiceProxyFactory;
import com.ddmap.push.test.listener.TestChannelListener;
import com.ddmap.push.test.service.TestService;
import com.ddmap.push.util.AsyncFutureInvoker;


public class Client {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		HsfConnector connector = new HsfConnectorImpl();
		connector.getListeners().add(new TestChannelListener());
		
		ChannelGroupFuture groupFuture = connector.connect(new InetSocketAddress("localhost", 8082));
		List<HsfChannelGroup> group = groupFuture.getGroupList();

		final TestService testService = ServiceProxyFactory.getRoundFactoryInstance(connector).wrapAsyncFutureProxy(
				TestService.class);
		AsyncFutureInvoker<TestService> futureInvoker = new AsyncFutureInvoker<TestService>(testService) {
			@Override
			protected void invokeService() {
				this.service.test("future invoker");
			}
		};
		InvokeFuture future = futureInvoker.invoke();
		System.out.println(future.getResult());
	}
}
