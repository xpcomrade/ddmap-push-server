package com.ddmap.push.netty.service;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ddmap.push.future.ChannelGroupFuture;
import com.ddmap.push.future.ChannelGroupFutureHolder;
import com.ddmap.push.jmx.management.annotation.Description;
import com.ddmap.push.jmx.management.annotation.ManagedAttribute;
import com.ddmap.push.netty.channel.HsfChannel;
import com.ddmap.push.netty.channel.HsfChannelGroup;
import com.ddmap.push.netty.channel.HsfNioClientSocketChannelFactory;
import com.ddmap.push.netty.handler.StateCheckChannelHandler;
import com.ddmap.push.netty.handler.upstream.DispatchUpStreamHandler;
import com.ddmap.push.netty.handshake.ConnectorHandshakeProcessor;
import com.ddmap.push.netty.listener.ChannelEventListenerAdapter;
import com.ddmap.push.netty.listener.EventBehavior;
import com.ddmap.push.netty.listener.impl.ConnectorGroupMessageEventListener;
import com.ddmap.push.util.*;
import com.ddmap.push.util.ConnectManager.ConnectionInfo;

import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Title: HsfConnectorImpl.java
 * @Package com.ddmap.push.netty.service
 * @Description: HsfConnector实现类
 * @author guo
 * @date 2012-2-21 下午11:30:55
 * @version V1.0
 */
public class HsfConnectorImpl extends AbstractHsfService implements HsfConnector {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private ClientBootstrap bootstrap;
	private HashedWheelTimer idleTimer = new HashedWheelTimer();
	private ConnectorHandshakeProcessor handshakeProcessor;
	private ScheduledExecutorService reconnectTimer = createReconnectScheduler();
	private ScheduledFuture<?> reconnectScheduledFuture;
	private ConnectManager connectManager = new ConnectManager();
	private AtomicBoolean reconnecting;
	private ReentrantLock reconnLock = new ReentrantLock();

	public HsfConnectorImpl() {
		super();
	}

	public HsfConnectorImpl(Executor bossExecutor, int workerCount) {
		super(bossExecutor, workerCount);
	}

	public HsfConnectorImpl(Executor bossExecutor, Executor workerExecutor, int workerCount) {
		super(bossExecutor, workerExecutor, workerCount);
	}

	@Override
	protected void init() {
		//
		reconnecting = new AtomicBoolean(false);
		//
		setOption(HsfOptions.CHANNEL_NUM_PER_GROUP, 1 /** Runtime.getRuntime().availableProcessors() **/
		);
		setOption(HsfOptions.RECONNECT_INTERVAL, 10000);
		//
		super.init();
		//
		bootstrap = new ClientBootstrap(new HsfNioClientSocketChannelFactory(bossExecutor, workerExecutor, workerCount));
	}

	@Override
	protected void initSystemListener() {
		super.initSystemListener();
		getListeners().add(0, new ReconnectHandler());
		getListeners().add(0, new ConnectorGroupMessageEventListener(eventDispatcher));
	}

	public ChannelGroupFuture connect(SocketAddress... addressArray) {
		return connect(addressArray, true);
	}

	public ChannelGroupFuture connect(SocketAddress[] addressArray, boolean sync) {
		if (addressArray == null || addressArray.length == 0) {
			throw new IllegalArgumentException("addressArray can not be null or empty.");
		}
		// 地址去重
		Set<SocketAddress> addressSet = new HashSet<SocketAddress>();
		for (SocketAddress socketAddress : addressArray) {
			addressSet.add(socketAddress);
		}

		// 去掉已经建立过连接的地址
		Set<SocketAddress> connectedAddressSet = connectManager.getConnectedAddress();
		addressSet.removeAll(connectedAddressSet);
		Set<SocketAddress> disconnectedAddressSet = connectManager.getDisconnectAddress();
		addressSet.removeAll(disconnectedAddressSet);

		//
		if (addressSet.size() == 0) {
			return new ChannelGroupFuture(0);
		}

		// 设置Option
		Map<String, Object> options = getOptions();
		for (String key : options.keySet()) {
			bootstrap.setOption(key, options.get(key));
		}

		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();

				// 注册各种自定义Handler
				LinkedHashMap<String, ChannelHandler> handlers = getHandlers();
				for (String key : handlers.keySet()) {
					pipeline.addLast(key, handlers.get(key));
				}

				// 注册链路空闲检测Handler
				Integer writeIdleTime = LangUtil.parseInt(getOption(HsfOptions.WRITE_IDLE_TIME));
				Integer readIdleTime = LangUtil.parseInt(getOption(HsfOptions.READ_IDLE_TIME));
				if (writeIdleTime == null) {
					writeIdleTime = 10;
				}
				if (readIdleTime == null) {
					// 默认为写空闲的3倍
					readIdleTime = writeIdleTime * 3;
				}
				pipeline.addLast("timeout", new IdleStateHandler(idleTimer, readIdleTime, writeIdleTime, 0));
				pipeline.addLast("idleHandler", new StateCheckChannelHandler(HsfConnectorImpl.this));

				// 注册事件分发Handler
				pipeline.addLast("dispatchHandler", new DispatchUpStreamHandler(eventDispatcher));

				return pipeline;
			}
		});

		// 启动重连任务
		startReconnect(false);

		final ChannelGroupFuture groupFuture = new ChannelGroupFuture(addressSet.size());
		int channelNumPerGroup = LangUtil.parseInt(getOption(HsfOptions.CHANNEL_NUM_PER_GROUP), Runtime.getRuntime()
				.availableProcessors());
		//
		for (final SocketAddress socketAddress : addressSet) {
			for (int i = 0; i < channelNumPerGroup; i++) {
				TLSUtil.setData(HsfConstants.KEY_CURRENT_CHANNEL_FUTURE, groupFuture);
				ChannelFuture singleFuture = bootstrap.connect(socketAddress);
				singleFuture.addListener(new ChannelFutureListener() {

					public void operationComplete(ChannelFuture future) throws Exception {
						if (!future.isSuccess()) {
							ChannelGroupFutureHolder.remove(future.getChannel().getId());
							Throwable ex = future.getCause();
							groupFuture.addFailure(socketAddress, ex);
							// 将该地址加入重连列表
							connectManager.addDisconnectAddress(socketAddress);

							logger.error("connect to {} failed. error:{}", socketAddress,
									StackTraceUtil.getStackTrace(ex));
						}
					}
				});
			}
		}
		// sync
		if (sync) {
			try {
				long connectTimeout = LangUtil.parseLong(getOption(HsfOptions.CONNECT_TIMEOUT), 30000L);
				groupFuture.getGroupList(connectTimeout, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
			}
		}

		return groupFuture;
	}

	/**
	 * @Title: startReconnect
	 * @Description: 启动重连任务
	 * @param reset
	 *        是否重设定时器
	 * @return void 返回类型
	 */
	protected void startReconnect(boolean reset) {
		if (reset) {
			if (reconnectTimer != null) {
				reconnectTimer.shutdownNow();
			}
			reconnectTimer = Executors.newScheduledThreadPool(1);

			reconnecting.set(false);
		}

		final Integer reconnectInterval = LangUtil.parseInt(getOption(HsfOptions.RECONNECT_INTERVAL), 10000);
		if (!reconnecting.get()) {
			reconnecting.set(true);
			if (reconnectScheduledFuture != null) {
				reconnectScheduledFuture.cancel(true);
			}
			//
			reconnectScheduledFuture = reconnectTimer.scheduleAtFixedRate(new Runnable() {

				public void run() {
					try {
						reconnLock.lock();
						Set<SocketAddress> disconnAddressList = connectManager.getDisconnectAddress();
						for (final SocketAddress socketAddress : disconnAddressList) {
							Integer num = connectManager.getDisconnectNum(socketAddress);
							if (num == null) {
								continue;
							}

							logger.warn("地址{}需要重连{}次", socketAddress, num);
							for (int i = 0; i < num; i++) {
								// 如果不包含，则重新建立连接
								logger.warn("尝试重连到{}", socketAddress);
								connectManager.logConnect(socketAddress);

								ChannelFuture singleFuture = bootstrap.connect(socketAddress);
								singleFuture.addListener(new ChannelFutureListener() {

									@Override
									public void operationComplete(ChannelFuture future) throws Exception {
										if (future.isSuccess()) {
											logger.warn("重连到{}成功", socketAddress);
											// 先从重连列表中删除
											connectManager.countDownDisconnect(socketAddress);
										} else {
											if (logger.isDebugEnabled()) {
												logger.error("重连到{}失敗:{}", socketAddress, future.getCause());
											}
										}
									}
								});
								try {
									singleFuture.await(reconnectInterval / 2);
								} catch (Exception e) {
								}
							}
						}
					} catch (Exception e) {
						logger.error("reconnect schedule error:{}", StackTraceUtil.getStackTrace(e));
					} finally {
						reconnLock.unlock();
					}
				}
			}, reconnectInterval, reconnectInterval, TimeUnit.MILLISECONDS);
		}
	}

	private ScheduledExecutorService createReconnectScheduler() {
		return Executors.newScheduledThreadPool(1, new NamedThreadFactory("HsfReconnectScheduler", true));
	}

	@Override
	public void setOption(String opName, Object opValue) {
		super.setOption(opName, opValue);
		if (HsfOptions.RECONNECT_INTERVAL.equals(opName)) {
			//
			Integer reconnectInterval = LangUtil.parseInt(opValue);
			if (reconnectInterval == 0) {
				stopReconnectScheduler();
			} else if (reconnectInterval < 0) {
				throw new IllegalAccessError("reconnectInterval must great than 0.");
			} else {
				startReconnect(true);
			}
		}
	}

	// 停止重连任务
	private void stopReconnectScheduler() {
		//
		try {
			if (reconnectScheduledFuture != null) {
				reconnectScheduledFuture.cancel(true);
			}
		} catch (Exception e1) {
		}
		//
		try {
			if (reconnectTimer != null) {
				reconnectTimer.shutdownNow();
			}
		} catch (Exception e) {
		}
	}

	public void shutdown() {
		super.shutdown();
		//
		try {
			idleTimer.stop();
		} catch (Exception e) {
		}
		//
		stopReconnectScheduler();
		//
		if (bootstrap != null) {
			bootstrap.releaseExternalResources();
		}
	}

	public ConnectorHandshakeProcessor getHandshakeProcessor() {
		return handshakeProcessor;
	}

	public void setHandshakeProcessor(ConnectorHandshakeProcessor handshakeProcessor) {
		this.handshakeProcessor = handshakeProcessor;
	}

	@Override
	public void refreshIPList(SocketAddress... addressArray) {
		refreshIPList(false, addressArray);
	}

	@Override
	public void refreshIPList(boolean forceProcess, SocketAddress... addressArray) {
		if ((addressArray == null || addressArray.length == 0) && !forceProcess) {
			return;
		}
		//
		logger.warn("refreshIPList forceProcess:{}, address:{}", forceProcess, addressArray);

		List<SocketAddress> newAddresses = Arrays.asList(addressArray);
		List<SocketAddress> addList = new ArrayList<SocketAddress>();
		List<SocketAddress> removeList = new ArrayList<SocketAddress>();
		Set<SocketAddress> connectedAddresses = connectManager.getConnectedAddress();
		Set<SocketAddress> disconnAddressList = connectManager.getDisconnectAddress();

		// 查找新增的地址
		for (SocketAddress socketAddress : addressArray) {
			if (!connectedAddresses.contains(socketAddress) && !disconnAddressList.contains(socketAddress)) {
				addList.add(socketAddress);
			}
		}
		// 查找删除的地址
		for (SocketAddress connectedAddress : connectedAddresses) {
			if (!newAddresses.contains(connectedAddress)) {
				removeList.add(connectedAddress);
			}
		}
		for (SocketAddress disconnectedAddress : disconnAddressList) {
			if (!newAddresses.contains(disconnectedAddress)) {
				removeList.add(disconnectedAddress);
			}
		}

		// 为删除的地址关闭连接
		for (SocketAddress socketAddress : removeList) {
			shutdown(socketAddress);
		}
		// 为新增的地址建立连接
		if (addList.size() > 0) {
			SocketAddress[] addresses = new SocketAddress[addList.size()];
			addList.toArray(addresses);

			connect(addresses);
		}
	}

	@Override
	public void shutdown(SocketAddress address) {
		logger.warn("shutdown address:{}", address);

		// 从重连列表中删除
		connectManager.removeDisconnect(address);

		String groupName = connectManager.getConnectedGroupName(address);
		if (groupName == null) {
			return;
		}

		HsfChannelGroup group = getGroups().get(groupName);
		if (group == null) {
			return;
		}

		group.close(true);
	}

	@ManagedAttribute
	@Description("Returns the disconnected addresses.")
	public Set<String> getDisconnectedAddresses() {
		Set<String> set = new HashSet<String>();
		Set<SocketAddress> disconnSet = connectManager.getDisconnectAddress();
		if (disconnSet != null) {
			for (SocketAddress add : disconnSet) {
				set.add(add.toString());
			}
		}
		return set;
	}

	@ManagedAttribute
	@Description("Returns the connected addresses.")
	public Set<String> getConnectedAddresses() {
		Set<String> set = new HashSet<String>();
		Set<SocketAddress> connSet = connectManager.getConnectedAddress();
		if (connSet != null) {
			for (SocketAddress add : connSet) {
				set.add(add.toString());
			}
		}
		return set;
	}

	@ManagedAttribute
	@Description("Returns the lastest 20 times reconnect addresses.")
	public Map<String, String> getReconnectInfo() {
		Queue<ConnectionInfo> queue = connectManager.getReconnectionInfoQueue();
		//
		Object[] array = queue.toArray();
		Map<String, String> map = new LinkedHashMap<String, String>();
		for (int i = array.length - 1; i >= 0; --i) {
			ConnectionInfo connInfo = (ConnectionInfo) array[i];
			map.put(connInfo.getTime(), connInfo.getAddress());
		}
		return map;
	}

	/**
	 * @ClassName: ReconnectHandler
	 * @Description: 重连处理实现
	 * @date 2011-9-29 下午12:17:30
	 * 
	 */
	protected class ReconnectHandler extends ChannelEventListenerAdapter {

		@Override
		public EventBehavior channelClosed(ChannelHandlerContext ctx, HsfChannel channel, ChannelStateEvent e) {
			if (channel.isStopReconnect()) {
				return EventBehavior.Continue;
			}

			// 将该地址加入重连列表
			connectManager.addDisconnectAddress(channel.getRemoteAddress());

			return EventBehavior.Continue;
		}

		@Override
		public EventBehavior groupCreated(ChannelHandlerContext ctx, HsfChannel channel, String groupName) {
			// 添加已连接地址
			connectManager.addConnected(channel.getRemoteAddress(), groupName);

			return EventBehavior.Continue;
		}

		@Override
		public EventBehavior groupRemoved(ChannelHandlerContext ctx, HsfChannel channel, String groupName) {
			// 删除已连接地址
			connectManager.removeConnected(channel.getRemoteAddress());

			return EventBehavior.Continue;
		}
	}
}
