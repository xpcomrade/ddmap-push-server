package com.ddmap.push.test.serviceargs;

import java.util.ArrayList;

import com.ddmap.push.netty.service.HsfConnector;
import com.ddmap.push.netty.service.HsfConnectorImpl;
import com.ddmap.push.proxy.ServiceProxyFactory;
import com.ddmap.push.util.AddressUtil;

/**
 * @Description: TODO
 * @author yangguo
 * @date 2012-5-8 下午6:10:55
 */
public class Client {
	public static void main(String[] args) {
		HsfConnector connector = new HsfConnectorImpl();
		connector.connect(AddressUtil.parseAddress("127.0.0.1:8088"));

		ArgService service = ServiceProxyFactory.getRoundFactoryInstance(connector).wrapSyncProxy(ArgService.class);
		System.out.println(service.testArgs(new ArrayList<String>(), new ArrayList<String>(), null, null, true));
	}
}
