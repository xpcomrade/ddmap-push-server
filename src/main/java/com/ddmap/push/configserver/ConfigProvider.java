package com.ddmap.push.configserver;

import com.ddmap.push.configserver.pojo.ConfigServiceInfo;
import com.ddmap.push.netty.service.HsfConnector;

/**
 * @Title: ConfigProvider.java
 * @Package com.ddmap.push.configserver
 * @Description: 配置服务提供者
 * @author guo
 * @date 2012-3-22 下午8:42:44
 * @version V1.0
 */
public interface ConfigProvider extends HsfConnector {
	public void setConfigService(ConfigServiceInfo configServiceInfo);

	public ConfigServiceInfo getConfigService();
	
	public void register(ConfigServiceInfo configServiceInfo);

	public void remove(ConfigServiceInfo configServiceInfo);
	
}
