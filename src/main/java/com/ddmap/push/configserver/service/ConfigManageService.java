package com.ddmap.push.configserver.service;

import com.ddmap.push.annotation.RemoteServiceContract;

/**
 * @Title: ConfigManageService.java
 * @Package com.ddmap.push.configserver.service
 * @Description: 配置服务管理接口
 * @author guo
 * @date 2012-3-22 上午11:54:45
 * @version V1.0
 */
@RemoteServiceContract
public interface ConfigManageService extends ConfigProviderService {
	public void clear();
}
