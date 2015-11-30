package com.ddmap.push.configserver;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Executor;

import org.jboss.netty.channel.ChannelHandlerContext;
import com.ddmap.push.configserver.pojo.ConfigServiceInfo;
import com.ddmap.push.configserver.pojo.ConfigServiceItemInfo;
import com.ddmap.push.configserver.service.ConfigProviderService;
import com.ddmap.push.netty.channel.HsfChannel;
import com.ddmap.push.netty.listener.ChannelEventListenerAdapter;
import com.ddmap.push.netty.listener.EventBehavior;
import com.ddmap.push.netty.service.HsfConnectorImpl;
import com.ddmap.push.netty.service.HsfService;
import com.ddmap.push.proxy.ServiceProxyFactory;
import com.ddmap.push.util.HsfContextHolder;

/**
 * @Title: ConfigProvider.java
 * @Package com.ddmap.push.configserver
 * @Description: 配置服务提供者实现
 * @author guo
 * @date 2012-3-22 下午8:40:04
 * @version V1.0
 */
public class ConfigProviderImpl extends HsfConnectorImpl implements ConfigProvider {
	private ConfigServiceInfo configServiceInfo;

	public ConfigProviderImpl() {
		super();
	}

	public ConfigProviderImpl(Executor bossExecutor, int workerCount) {
		super(bossExecutor, workerCount);
	}

	public ConfigProviderImpl(Executor bossExecutor, Executor workerExecutor, int workerCount) {
		super(bossExecutor, workerExecutor, workerCount);
	}

	@Override
	protected void init() {
		super.init();
		getListeners().add(new CSChannelEventListener());
	}

	@Override
	public void register(ConfigServiceInfo configServiceInfo) {
		// 向ConfigServer注册服务
		ConfigProviderService providerProxyService = ServiceProxyFactory.getBroadcastFactoryInstance(this)
				.wrapSyncProxy(ConfigProviderService.class);
		providerProxyService.register(configServiceInfo);

		for (Map.Entry<String, HashSet<ConfigServiceItemInfo>> entry : configServiceInfo.getItems().entrySet()) {
			this.configServiceInfo.addItems(entry.getValue());
		}
	}

	@Override
	public void remove(ConfigServiceInfo configServiceInfo) {
		// 向ConfigServer注册服务
		ConfigProviderService providerProxyService = ServiceProxyFactory.getBroadcastFactoryInstance(this)
				.wrapSyncProxy(ConfigProviderService.class);
		providerProxyService.remove(configServiceInfo);

		for (Map.Entry<String, HashSet<ConfigServiceItemInfo>> entry : configServiceInfo.getItems().entrySet()) {
			this.configServiceInfo.removeItems(entry.getValue());
		}
	}

	@Override
	public void setConfigService(ConfigServiceInfo configServiceInfo) {
		this.configServiceInfo = configServiceInfo;
	}

	@Override
	public ConfigServiceInfo getConfigService() {
		return configServiceInfo;
	}

	private class CSChannelEventListener extends ChannelEventListenerAdapter {
		@Override
		public EventBehavior groupCreated(ChannelHandlerContext ctx, HsfChannel channel, String groupName) {
			HsfService hsfService = HsfContextHolder.getHsfService();
			// 向ConfigServer注册服务
			ConfigProviderService providerProxyService = ServiceProxyFactory.getRoundFactoryInstance(hsfService)
					.wrapSyncProxy(ConfigProviderService.class, groupName);
			providerProxyService.register(configServiceInfo);

			return EventBehavior.Continue;
		}
	}
}
