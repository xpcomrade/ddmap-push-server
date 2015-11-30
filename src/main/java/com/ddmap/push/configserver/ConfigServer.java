package com.ddmap.push.configserver;

import java.util.concurrent.Executor;

import com.ddmap.push.configserver.listener.ConfigServerChannelListener;
import com.ddmap.push.configserver.service.impl.ConfigConsumerServiceImpl;
import com.ddmap.push.configserver.service.impl.ConfigManageServiceImpl;
import com.ddmap.push.configserver.service.impl.ConfigProviderServiceImpl;
import com.ddmap.push.netty.service.HsfAcceptorImpl;

/**
 * @Title: ConfigServer.java
 * @Package com.ddmap.push.configserver
 * @Description: ConfigServer
 * @author guo
 * @date 2012-3-22 下午8:22:09
 * @version V1.0
 */
public class ConfigServer extends HsfAcceptorImpl {
	public ConfigServer() {
		super();
	}

	public ConfigServer(Executor bossExecutor, int workerCount) {
		super(bossExecutor, workerCount);
	}

	public ConfigServer(Executor bossExecutor, Executor workerExecutor, int workerCount) {
		super(bossExecutor, workerExecutor, workerCount);
	}

	@Override
	protected void initSystemListener() {
		super.initSystemListener();
		getListeners().add(0, new ConfigServerChannelListener());
	}

	@Override
	protected void init() {
		super.init();
		// 注册配置服务
		registerService(new ConfigConsumerServiceImpl());
		registerService(new ConfigProviderServiceImpl());
		registerService(new ConfigManageServiceImpl());
	}
}
