package com.ddmap.push.test.reconnect;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

import com.ddmap.push.netty.service.HsfConnector;
import com.ddmap.push.netty.service.HsfConnectorImpl;

/**
 * @Title: Client.java
 * @Description: TODO(添加描述)
 * @date 2012-2-23 上午01:01:33
 * @version V1.0
 */
public class Client {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		HsfConnector connector = new HsfConnectorImpl();
		connector.connect(new InetSocketAddress("127.0.0.1", 8082));
		//
		System.out.println("try to shudown");
		//
		connector.shutdown();
	}

}
