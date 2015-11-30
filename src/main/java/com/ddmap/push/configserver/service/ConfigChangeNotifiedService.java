package com.ddmap.push.configserver.service;

import com.ddmap.push.annotation.RemoteServiceContract;
import com.ddmap.push.configserver.pojo.ConfigServiceInfo;

/**
 * @Title: ConfigChangeNotifiedService.java
 * @Package com.ddmap.push.configserver.service
 * @Description: 配置服务变更通知服务接口
 * @author guo
 * @date 2012-3-22 下午1:26:08
 * @version V1.0
 */
@RemoteServiceContract
public interface ConfigChangeNotifiedService {
	public void configChanged(ConfigServiceInfo configServiceInfo);
}
