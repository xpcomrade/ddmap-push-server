package com.ddmap.push.test.inversion;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

import com.ddmap.push.netty.service.HsfConnector;
import com.ddmap.push.netty.service.HsfConnectorImpl;
import com.ddmap.push.test.service.TestServiceImpl;

/**
 * @Title: Client.java
 * @Description: TODO(添加描述)
 * @date 2012-2-23 上午01:01:33
 * @version V1.0
 */
public class Client {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		HsfConnector connector = new HsfConnectorImpl();
		connector.registerService(new TestServiceImpl());
		connector.connect(new InetSocketAddress("192.168.1.52", 8082));
	}

}
