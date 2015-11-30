package com.ddmap.push.configserver.service;

import com.ddmap.push.annotation.RemoteServiceContract;
import com.ddmap.push.configserver.pojo.ConfigServiceInfo;

/**
 * @Title: ConfigProviderService.java
 * @Package com.ddmap.push.configserver.service
 * @Description: 配置服务提供者接口
 * @author guo
 * @date 2012-3-22 上午11:54:45
 * @version V1.0
 */
@RemoteServiceContract
public interface ConfigProviderService extends ConfigService {
	/**
	 * @Title: register
	 * @Description: 注册服务
	 * @author guo
	 * @param configServiceInfo
	 *        设定文件
	 * @return void 返回类型
	 */
	public void register(ConfigServiceInfo configServiceInfo);

	/**
	 * @Title: register
	 * @Description: 删除服务
	 * @author guo
	 * @param configServiceInfo
	 *        设定文件
	 * @return void 返回类型
	 */
	public void remove(ConfigServiceInfo configServiceInfo);
}
