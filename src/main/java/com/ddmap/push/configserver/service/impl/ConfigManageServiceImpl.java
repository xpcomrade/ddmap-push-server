package com.ddmap.push.configserver.service.impl;

import com.ddmap.push.configserver.ConfigManager;
import com.ddmap.push.configserver.service.ConfigManageService;

/**
 * @Title: ConfigManageServiceImpl.java
 * @Package com.ddmap.push.configserver.service.impl
 * @Description: 配置服务管理实现类
 * @author guo
 * @date 2012-3-22 下午2:42:35
 * @version V1.0
 */
public class ConfigManageServiceImpl extends ConfigProviderServiceImpl implements ConfigManageService {

	@Override
	public void clear() {
		//
		ConfigManager configManager = ConfigManager.getConfigManager();
		configManager.clear();
	}

}
