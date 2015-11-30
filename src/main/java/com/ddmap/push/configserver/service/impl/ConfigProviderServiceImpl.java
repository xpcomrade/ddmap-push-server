package com.ddmap.push.configserver.service.impl;

import java.util.HashSet;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ddmap.push.configserver.ConfigManager;
import com.ddmap.push.configserver.pojo.ConfigServiceInfo;
import com.ddmap.push.configserver.pojo.ConfigServiceItemInfo;
import com.ddmap.push.configserver.service.ConfigChangeNotifiedService;
import com.ddmap.push.configserver.service.ConfigProviderService;
import com.ddmap.push.netty.service.HsfService;
import com.ddmap.push.proxy.ServiceProxyFactory;
import com.ddmap.push.util.HsfContextHolder;
import com.ddmap.push.util.StackTraceUtil;

/**
 * @Title: ConfigProviderServiceImpl.java
 * @Package com.ddmap.push.configserver.service.impl
 * @Description: 配置服务提供者实现类
 * @author guo
 * @date 2012-3-22 下午1:34:24
 * @version V1.0
 */
public class ConfigProviderServiceImpl extends ConfigServiceImpl implements ConfigProviderService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void register(ConfigServiceInfo configServiceInfo) {
		//
		ConfigManager configManager = ConfigManager.getConfigManager();
		if (configManager.update(configServiceInfo)) {
			// 通知所有订阅的客户端
			notifyChanged(configServiceInfo);
		}
	}

	@Override
	public void remove(ConfigServiceInfo configServiceInfo) {
		ConfigManager configManager = ConfigManager.getConfigManager();
		if (configManager.remove(configServiceInfo)) {
			// 通知所有订阅的客户端
			notifyChanged(configServiceInfo);
		}
	}

	private void notifyChanged(ConfigServiceInfo configServiceInfo) {
		HsfService hsfService = HsfContextHolder.getHsfService();
		ConfigManager configManager = ConfigManager.getConfigManager();
		//
		Map<String, HashSet<ConfigServiceItemInfo>> items = configServiceInfo.getItems();
		for (Map.Entry<String, HashSet<ConfigServiceItemInfo>> entry : items.entrySet()) {
			HashSet<String> remoteGroup = configManager.getSubscribeGroups(entry.getKey());
			if (remoteGroup != null) {
				//
				for (String group : remoteGroup) {
					ConfigChangeNotifiedService proxyService = ServiceProxyFactory.getRoundFactoryInstance(hsfService)
							.wrapAsyncProxy(ConfigChangeNotifiedService.class, group);
					ConfigServiceInfo cfgInfo = new ConfigServiceInfo();
					//
					HashSet<String> serviceSet = configManager.getSubscribeServices(group);
					if (serviceSet != null) {
						for (String service : serviceSet) {
							HashSet<ConfigServiceItemInfo> serviceInfo = configManager.getConfigMap().get(service);
							cfgInfo.addItems(serviceInfo);
						}
					}
					try {
						proxyService.configChanged(cfgInfo);
					} catch (Exception e) {
						logger.error(StackTraceUtil.getStackTrace(e));
					}
				}
			}
		}
	}

}
