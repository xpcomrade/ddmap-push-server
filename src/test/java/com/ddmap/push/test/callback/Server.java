package com.ddmap.push.test.callback;

import com.ddmap.push.netty.service.HsfAcceptor;
import com.ddmap.push.netty.service.HsfAcceptorImpl;
import com.ddmap.push.test.listener.TestChannelListener;
import com.ddmap.push.test.service.TestServiceImpl;

public class Server {
	public static void main(String[] args) {
		HsfAcceptor acceptor = new HsfAcceptorImpl();
		acceptor.getListeners().add(new TestChannelListener());
		acceptor.registerService(new TestServiceImpl());
		acceptor.bind(8082);
	}
}
