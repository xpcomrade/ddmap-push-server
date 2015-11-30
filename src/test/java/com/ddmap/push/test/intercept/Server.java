package com.ddmap.push.test.intercept;

import java.util.LinkedList;

import com.ddmap.push.netty.event.EventDispatcher;
import com.ddmap.push.netty.interceptor.PreDispatchInterceptor;
import com.ddmap.push.netty.interceptor.ServicePreDispatchInterceptorAdpator;
import com.ddmap.push.netty.service.HsfAcceptor;
import com.ddmap.push.netty.service.HsfAcceptorImpl;
import com.ddmap.push.pojo.RemoteServiceMessage;
import com.ddmap.push.pojo.RemoteServiceObject;
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
		LinkedList<PreDispatchInterceptor> interceptors = new LinkedList<PreDispatchInterceptor>();
		interceptors.add(new ServicePreDispatchInterceptorAdpator() {
			
			@Override
			protected boolean innerIntercept(EventDispatcher eventDispatcher, RemoteServiceObject msg) {
				return false;
			}
		});
		acceptor.setPreDispatchInterceptors(interceptors);
		acceptor.registerService(new TestServiceImpl());
		
		acceptor.bind(8082);
	}
}
