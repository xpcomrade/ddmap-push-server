package com.ddmap.push.configserver.service;

import java.util.Set;

import com.ddmap.push.annotation.RemoteServiceContract;
import com.ddmap.push.configserver.pojo.ConfigServiceInfo;
import com.ddmap.push.configserver.pojo.ConfigServiceItemInfo;

/**
 * @Title: ConfigService.java
 * @Package com.ddmap.push.configserver.service
 * @Description: 配置服务接口
 * @author guo
 * @date 2012-3-22 上午11:54:45
 * @version V1.0
 */
@RemoteServiceContract
public interface ConfigService {
	public ConfigServiceInfo getConfigService();

	public Set<ConfigServiceItemInfo> getConfigService(String serviceName);
}
