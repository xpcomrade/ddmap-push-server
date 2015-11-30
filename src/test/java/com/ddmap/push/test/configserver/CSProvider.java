package com.ddmap.push.test.configserver;

import com.ddmap.push.configserver.ConfigProvider;
import com.ddmap.push.configserver.ConfigProviderImpl;
import com.ddmap.push.configserver.pojo.ConfigServiceInfo;
import com.ddmap.push.configserver.pojo.ConfigServiceItemInfo;
import com.ddmap.push.netty.service.HsfAcceptor;
import com.ddmap.push.netty.service.HsfAcceptorImpl;
import com.ddmap.push.test.service.TestServiceImpl;
import com.ddmap.push.util.AddressUtil;
import com.ddmap.push.util.HsfOptions;

/**
 * 配置服务提供者
 * 
 * @Title: CSProvider.java
 * @Package com.ddmap.push.test.configserver
 * @date 2012-3-22 下午8:38:58
 * @version V1.0
 */
public class CSProvider {
	public static void main(String[] args) {
		HsfAcceptor acceptor = new HsfAcceptorImpl();
		acceptor.registerService(new TestServiceImpl());
		acceptor.bind(8090);
		//
		ConfigProvider configProvider = new ConfigProviderImpl();
		configProvider.setOption(HsfOptions.SYNC_INVOKE_TIMEOUT, 600000);
		configProvider.setOption(HsfOptions.HANDSHAKE_TIMEOUT, 600000);
		//
		ConfigServiceInfo configServiceInfo = new ConfigServiceInfo();
		configServiceInfo.addItem(new ConfigServiceItemInfo("TestService", "127.0.0.1:8090"));
		configProvider.setConfigService(configServiceInfo);
		//
		configProvider.connect(AddressUtil.parseAddress("127.0.0.1:8082"));
	}
}
