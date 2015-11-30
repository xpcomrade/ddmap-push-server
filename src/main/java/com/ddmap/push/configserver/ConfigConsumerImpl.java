package com.ddmap.push.configserver;

import com.ddmap.push.configserver.pojo.ConfigServiceInfo;
import com.ddmap.push.configserver.pojo.ConfigServiceItemInfo;
import com.ddmap.push.configserver.service.ConfigChangeNotifiedService;
import com.ddmap.push.configserver.service.ConfigConsumerService;
import com.ddmap.push.netty.channel.HsfChannel;
import com.ddmap.push.netty.listener.ChannelEventListenerAdapter;
import com.ddmap.push.netty.listener.EventBehavior;
import com.ddmap.push.netty.service.HsfConnector;
import com.ddmap.push.netty.service.HsfConnectorImpl;
import com.ddmap.push.netty.service.HsfService;
import com.ddmap.push.proxy.ServiceProxyFactory;
import com.ddmap.push.util.AddressUtil;
import com.ddmap.push.util.HsfContextHolder;
import com.ddmap.push.util.StackTraceUtil;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.Executor;

/**
 * @Title: ConfigConsumerImpl.java
 * @Package com.ddmap.push.configserver
 * @Description: 配置服务使用者实现
 * @author guo
 * @date 2012-3-22 下午9:13:57
 * @version V1.0
 */
public class ConfigConsumerImpl extends HsfConnectorImpl implements ConfigConsumer {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private HsfConnector connector;
	private ConfigServiceInfo configServiceInfo;
	private Set<String> subscribeService = new HashSet<String>();

	public ConfigConsumerImpl() {
		super();
	}

	public ConfigConsumerImpl(Executor bossExecutor, int workerCount) {
		super(bossExecutor, workerCount);
	}

	public ConfigConsumerImpl(Executor bossExecutor, Executor workerExecutor, int workerCount) {
		super(bossExecutor, workerExecutor, workerCount);
	}

	@Override
	protected void initSystemListener() {
		super.initSystemListener();
		//
		getListeners().add(new CSChannelEventListener());
		registerService(new ConfigChangeNotifiedServiceImpl());
	}

	@Override
	public Set<String> subscribe(String... serviceArray) {
		if (serviceArray == null || serviceArray.length == 0) {
			return subscribeService;
		}
		for (String service : serviceArray) {
			subscribeService.add(service);
		}

		//
		ConfigConsumerService consumerProxyService = ServiceProxyFactory.getBroadcastFactoryInstance(this)
				.wrapSyncProxy(ConfigConsumerService.class);
		//
		try {
			consumerProxyService.subscribe(serviceArray);
		} catch (Exception e) {
			logger.error("subscribe to config server failed, error:{}", StackTraceUtil.getStackTrace(e));
		}
		return subscribeService;
	}

	@Override
	public Set<String> unsubscribe(String... serviceArray) {
		if (serviceArray == null || serviceArray.length == 0) {
			return subscribeService;
		}
		for (String service : serviceArray) {
			subscribeService.remove(service);
		}
		//
		ConfigConsumerService consumerProxyService = ServiceProxyFactory.getBroadcastFactoryInstance(this)
				.wrapSyncProxy(ConfigConsumerService.class);
		//
		try {
			consumerProxyService.unsubscribe(serviceArray);
		} catch (Exception e) {
			logger.error("subscribe to config server failed, error:{}", StackTraceUtil.getStackTrace(e));
		}
		return subscribeService;
	}

	@Override
	public void setSubscribeService(Set<String> subscribeService) {
		this.subscribeService = subscribeService;
	}

	@Override
	public Set<String> getSubscribeService() {
		return subscribeService;
	}

	@Override
	public ConfigServiceInfo getConfigServiceInfo() {
		return configServiceInfo;
	}

	@Override
	public void setConnector(HsfConnector connector) {
		this.connector = connector;
	}

	@Override
	public HsfConnector getConnector() {
		return connector;
	}

	private class CSChannelEventListener extends ChannelEventListenerAdapter {
		@Override
		public EventBehavior groupCreated(ChannelHandlerContext ctx, HsfChannel channel, String groupName) {
			if (subscribeService != null && subscribeService.size() > 0) {
				//
				HsfService hsfService = HsfContextHolder.getHsfService();
				ConfigConsumerService consumerProxyService = ServiceProxyFactory.getRoundFactoryInstance(hsfService)
						.wrapSyncProxy(ConfigConsumerService.class, groupName);
				//
				String[] serviceArray = new String[subscribeService.size()];
				subscribeService.toArray(serviceArray);
				try {
					consumerProxyService.subscribe(serviceArray);
				} catch (Exception e) {
					logger.error("subscribe to config server failed, error:{}", StackTraceUtil.getStackTrace(e));
				}
			}
			return EventBehavior.Continue;
		}
	}

	public final class ConfigChangeNotifiedServiceImpl implements ConfigChangeNotifiedService {

		@Override
		public void configChanged(ConfigServiceInfo configServiceInfo) {
			ConfigConsumerImpl.this.configServiceInfo = configServiceInfo;
			if (connector != null) {
				if (configServiceInfo == null) {
					return;
				}
				//
				Collection<HashSet<ConfigServiceItemInfo>> serviceConfigList = configServiceInfo.getItems().values();
				if (serviceConfigList == null) {
					return;
				}
				//
				List<SocketAddress> addressList = new ArrayList<SocketAddress>();
				for (HashSet<ConfigServiceItemInfo> itemList : serviceConfigList) {
					for (ConfigServiceItemInfo item : itemList) {
						SocketAddress[] addressArray = AddressUtil.parseAddress(item.getAddress());
						for (SocketAddress socketAddress : addressArray) {
							addressList.add(socketAddress);
						}
					}
				}

				//
				SocketAddress[] addressArray = new SocketAddress[addressList.size()];
				addressList.toArray(addressArray);
				connector.refreshIPList(true, addressArray);
			}
		}
	}
}
