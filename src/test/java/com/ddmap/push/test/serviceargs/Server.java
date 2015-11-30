package com.ddmap.push.test.serviceargs;

import com.ddmap.push.netty.service.HsfAcceptor;
import com.ddmap.push.netty.service.HsfAcceptorImpl;

/**
 * @Title: Server.java
 * @Package com.ddmap.push.test.phone
 * @date 2012-3-19 下午10:48:42
 * @version V1.0
 */
public class Server {
	public static void main(String[] args) {
		HsfAcceptor acceptor = new HsfAcceptorImpl();
		acceptor.registerService(new ArgServiceImpl());

		acceptor.bind(8088);
	}

}
