package com.ddmap.push.test.normal;

import com.ddmap.push.netty.service.HsfAcceptor;
import com.ddmap.push.netty.service.HsfAcceptorImpl;
import com.ddmap.push.test.listener.TestChannelListener;
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
		acceptor.getListeners().add(new TestChannelListener());
		acceptor.registerService(new TestServiceImpl());
		acceptor.bind(8082);
	}
}
