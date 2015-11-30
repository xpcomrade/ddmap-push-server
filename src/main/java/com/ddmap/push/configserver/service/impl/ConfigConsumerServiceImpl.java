package com.ddmap.push.configserver.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ddmap.push.configserver.ConfigManager;
import com.ddmap.push.configserver.pojo.ConfigServiceInfo;
import com.ddmap.push.configserver.pojo.ConfigServiceItemInfo;
import com.ddmap.push.configserver.service.ConfigChangeNotifiedService;
import com.ddmap.push.configserver.service.ConfigConsumerService;
import com.ddmap.push.netty.service.HsfService;
import com.ddmap.push.proxy.ServiceProxyFactory;
import com.ddmap.push.util.HsfContextHolder;
import com.ddmap.push.util.StackTraceUtil;

/**
 * @Title: ConfigConsumerServiceImpl.java
 * @Package com.ddmap.push.configserver.service.impl
 * @Description: 配置服务消费者实现类
 * @author guo
 * @date 2012-3-22 下午2:40:23
 * @version V1.0
 */
public class ConfigConsumerServiceImpl extends ConfigServiceImpl implements ConfigConsumerService {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public Set<String> subscribe(String... serviceArray) {
		//
		ConfigManager configManager = ConfigManager.getConfigManager();
		String groupName = HsfContextHolder.getRemoteGroupName();
		//
		logger.warn("{} subscribe service;{}", groupName, serviceArray);
		configManager.subscribe(groupName, serviceArray);
		//
		Set<String> subscribeSet = configManager.getSubscribeServices(groupName);
		// 通知
		notifyChanged(groupName);

		return subscribeSet;
	}

	@Override
	public Set<String> unsubscribe(String... serviceArray) {
		//
		ConfigManager configManager = ConfigManager.getConfigManager();
		String groupName = HsfContextHolder.getRemoteGroupName();
		//
		logger.warn("{} unsubscribe service;{}", groupName, serviceArray);
		configManager.unsubscribe(groupName, serviceArray);
		//
		Set<String> subscribeSet = configManager.getSubscribeServices(groupName);
		// 通知
		notifyChanged(groupName);
		//
		return subscribeSet;
	}

	private void notifyChanged(String groupName) {
		HsfService hsfService = HsfContextHolder.getHsfService();
		ConfigManager configManager = ConfigManager.getConfigManager();
		//
		ConfigServiceInfo configServiceInfo = new ConfigServiceInfo();
		Set<String> subscribeSet = configManager.getSubscribeServices(groupName);
		//
		if (subscribeSet != null) {
			for (String service : subscribeSet) {
				HashSet<ConfigServiceItemInfo> serviceSet = configManager.getConfigMap().get(service);
				if (serviceSet != null) {
					configServiceInfo.addItems(serviceSet);
				}
			}
		}
		//
		ConfigChangeNotifiedService proxyService = ServiceProxyFactory.getRoundFactoryInstance(hsfService)
				.wrapAsyncProxy(ConfigChangeNotifiedService.class, groupName);
		//
		try {
			proxyService.configChanged(configServiceInfo);
			//
			logger.info("notify group {} service;{}", groupName, configServiceInfo);
		} catch (Exception e) {
			logger.error("notify failed group {} service;{}, error:{}", new Object[] { groupName, configServiceInfo,
					StackTraceUtil.getStackTrace(e) });
		}
	}
}
