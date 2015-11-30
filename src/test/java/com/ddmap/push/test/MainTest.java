package com.ddmap.push.test;

import java.net.InetSocketAddress;

import com.ddmap.push.netty.service.HsfConnector;
import com.ddmap.push.netty.service.HsfConnectorImpl;
import com.ddmap.push.test.service.TestServiceImpl;

/**
 * @Description: TODO
 * @author yangguo
 * @date 2012-5-14 上午10:15:45
 */
public class MainTest {
	
	public static void main(String[] args) {
		
		HsfConnector connector = new HsfConnectorImpl();
		connector.connect(new InetSocketAddress("192.168.11.3", 8002));
		connector.registerService(new TestServiceImpl());
	}
}
