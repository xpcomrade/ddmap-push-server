package com.ddmap.push.configserver.service.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.ddmap.push.configserver.ConfigManager;
import com.ddmap.push.configserver.pojo.ConfigServiceInfo;
import com.ddmap.push.configserver.pojo.ConfigServiceItemInfo;
import com.ddmap.push.configserver.service.ConfigService;

/**
 * @Title: ConfigServiceImpl.java
 * @Package com.ddmap.push.configserver.service.impl
 * @Description: 配置服务
 * @author guo
 * @date 2012-3-22 下午1:35:13
 * @version V1.0
 */
public class ConfigServiceImpl implements ConfigService {

	@Override
	public ConfigServiceInfo getConfigService() {
		//
		ConfigManager configManager = ConfigManager.getConfigManager();
		ConcurrentHashMap<String, HashSet<ConfigServiceItemInfo>> configMap = configManager.getConfigMap();
		//
		ConfigServiceInfo configServiceInfo = new ConfigServiceInfo();
		configServiceInfo.setItems(configMap);
		return configServiceInfo;
	}

	@Override
	public Set<ConfigServiceItemInfo> getConfigService(String serviceName) {
		//
		ConfigManager configManager = ConfigManager.getConfigManager();
		ConcurrentHashMap<String, HashSet<ConfigServiceItemInfo>> configMap = configManager.getConfigMap();
		//
		return configMap.get(serviceName);
	}

}
