package com.ddmap.push.test.flow;

import java.util.concurrent.atomic.AtomicInteger;

import com.ddmap.push.netty.service.HsfAcceptor;
import com.ddmap.push.netty.service.HsfAcceptorImpl;
import com.ddmap.push.test.service.TestService;
import com.ddmap.push.test.service.TestServiceImpl;
import com.ddmap.push.util.HsfOptions;


/**
 * @Title: Server.java
 * @Description: TODO(添加描述)
 * @date 2012-2-23 上午12:58:53
 * @version V1.0
 */
public class Server {
	public static void main(String[] args) {
		HsfAcceptor acceptor = new HsfAcceptorImpl();
		acceptor.registerService(new TestService() {
			private AtomicInteger num = new AtomicInteger();
			
			@Override
			public String test(String ctx) {
				try {
					Thread.sleep(5000L);
				} catch (InterruptedException e) {
				}
				return String.valueOf(num.incrementAndGet());
			}
		});
		
		acceptor.bind(8082);
	}
}
