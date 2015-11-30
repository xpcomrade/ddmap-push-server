package com.ddmap.push.proxy;

import com.ddmap.push.netty.dispatcher.async.AsyncBroadcastDispatchStrategy;
import com.ddmap.push.netty.dispatcher.async.AsyncDispatchStrategy;
import com.ddmap.push.netty.dispatcher.async.AsyncRoundDispatchStrategy;
import com.ddmap.push.netty.dispatcher.sync.SyncBroadcastDispatchStrategy;
import com.ddmap.push.netty.dispatcher.sync.SyncDispatchStrategy;
import com.ddmap.push.netty.dispatcher.sync.SyncRoundDispatchStrategy;
import com.ddmap.push.netty.service.HsfService;
import com.ddmap.push.util.AsyncCallback;

/**
 * @Title: NettyClientFactory.java
 * @Package com.ddmap.push.proxy
 * @Description: Service代理工厂
 * @author guo
 * @date 2011-11-19 下午10:05:26
 * @version V1.0
 */
public class ServiceProxyFactory {

	protected HsfService hsfService;
	protected SyncDispatchStrategy syncDispatchStrategy;
	protected AsyncDispatchStrategy asyncDispatchStrategy;

	protected ServiceProxyFactory(HsfService hsfService) {
		this.hsfService = hsfService;
		syncDispatchStrategy = new SyncRoundDispatchStrategy(hsfService);
		asyncDispatchStrategy = new AsyncRoundDispatchStrategy(hsfService);
	}

	public HsfService getHsfService() {
		return hsfService;
	}

	/**
	 * @Title: getRoundFactoryInstance
	 * @Description: 获取Round策略工厂实例
	 * @author guo
	 * @param hsfService
	 * @return ServiceProxyFactory 返回类型
	 */
	public static ServiceProxyFactory getRoundFactoryInstance(HsfService hsfService) {
		return new ServiceProxyFactory(hsfService);
	}

	/**
	 * @Title: getBroadcastFactoryInstance
	 * @Description: 获取Broadcast策略工厂实例
	 * @author guo
	 * @param hsfService
	 * @return ServiceProxyFactory 返回类型
	 */
	public static ServiceProxyFactory getBroadcastFactoryInstance(HsfService hsfService) {
		return new BroadcastServiceProxyFactory(hsfService);
	}

	/**
	 * @Title: wrapSyncProxy
	 * @Description: 为指定服务接口，创建同步代理对象。通过该对象调用远程服务时，当前线程将阻塞，直到远程服务返回结果
	 * @author guo
	 * @param serviceInterface
	 *        服务接口
	 * @return T 返回类型
	 */
	public <T> T wrapSyncProxy(Class<T> serviceInterface) {
		return RemoteServiceProxy.wrapSyncProxy(serviceInterface, syncDispatchStrategy);
	}

	/**
	 * @Title: wrapSyncProxy
	 * @Description: 为指定服务接口和Group，创建同步代理对象。通过该对象调用远程服务时，当前线程将阻塞，直到远程服务返回结果
	 * @author guo
	 * @param serviceInterface
	 *        服务接口
	 * @param groupName
	 *        目标Group
	 * @return T 返回类型
	 */
	public <T> T wrapSyncProxy(Class<T> serviceInterface, String groupName) {
		return RemoteServiceProxy.wrapSyncProxy(serviceInterface, hsfService, groupName);
	}

	/**
	 * @Title: wrapAsyncProxy
	 * @Description: 为指定服务接口，创建异步代理对象。通过该对象调用远程服务时，将直接返回类型的默认值(null)。当调用方不关心远程服务返回结果时，可使用此方法。
	 * @author guo
	 * @param serviceInterface
	 *        服务接口
	 * @return T 返回类型
	 */
	public <T> T wrapAsyncProxy(Class<T> serviceInterface) {
		return wrapAsyncCallbackProxy(serviceInterface, null);
	}

	/**
	 * @Title: wrapAsyncProxy
	 * @Description: 为指定服务接口和Group，创建异步代理对象。通过该对象调用远程服务时，将直接返回类型的默认值(null)。当调用方不关心远程服务返回结果时，可使用此方法。
	 * @author guo
	 * @param serviceInterface
	 *        服务接口
	 * @param groupName
	 *        目标Group
	 * @return T 返回类型
	 */
	public <T> T wrapAsyncProxy(Class<T> serviceInterface, String groupName) {
		return wrapAsyncCallbackProxy(serviceInterface, null, groupName);
	}

	/**
	 * @Title: wrapAsyncCallbackProxy
	 * @Description: 为指定服务接口，创建异步代理对象。通过该对象调用远程服务时，将直接返回类型的默认值(null)，远程服务返回的结果回调至指定Callback。
	 * @author guo
	 * @param serviceInterface
	 *        服务接口
	 * @param callback
	 *        回调
	 * @return T 返回类型
	 */
	public <T> T wrapAsyncCallbackProxy(Class<T> serviceInterface, AsyncCallback<?> callback) {
		return RemoteServiceProxy.wrapAsyncCallbackProxy(serviceInterface, callback, asyncDispatchStrategy);
	}

	/**
	 * @Title: wrapAsyncCallbackProxy
	 * @Description: 为指定服务接口和Group，创建异步代理对象。通过该对象调用远程服务时，将直接返回类型的默认值(null)，远程服务返回的结果回调至指定Callback。
	 * @author guo
	 * @param serviceInterface
	 *        服务接口
	 * @param callback
	 *        回调
	 * @param groupName
	 *        目标Group
	 * @return T 返回类型
	 */
	public <T> T wrapAsyncCallbackProxy(Class<T> serviceInterface, AsyncCallback<?> callback, String groupName) {
		return RemoteServiceProxy.wrapAsyncCallbackProxy(serviceInterface, callback, hsfService, groupName);
	}

	/**
	 * @Title: wrapAsyncFutureProxy
	 * @Description: 为指定服务接口，创建异步代理对象。请使用{@link AsyncFutureInvoker}调用方式，直接使用该对象，将与{@link #wrapAsyncProxy
	 *               wrapAsyncProxy(Class serviceInterface)}一至。
	 * @author guo
	 * @param serviceInterface
	 *        服务接口
	 * @return T 返回类型
	 */
	public <T> T wrapAsyncFutureProxy(Class<T> serviceInterface) {
		return RemoteServiceProxy.wrapAsyncFutureProxy(serviceInterface, asyncDispatchStrategy);
	}

	/**
	 * @Title: wrapAsyncFutureProxy
	 * @Description: 为指定服务接口和Group，创建异步代理对象。请使用{@link AsyncFutureInvoker}调用方式，直接使用该对象，将与{@link #wrapAsyncProxy
	 *               wrapAsyncProxy(Class serviceInterface, String groupName)}一至。
	 * @author guo
	 * @param serviceInterface
	 *        服务接口
	 * @param groupName
	 *        目标Group
	 * @return T 返回类型
	 */
	public <T> T wrapAsyncFutureProxy(Class<T> serviceInterface, String groupName) {
		return RemoteServiceProxy.wrapAsyncFutureProxy(serviceInterface, hsfService, groupName);
	}

	private static class BroadcastServiceProxyFactory extends ServiceProxyFactory {

		private BroadcastServiceProxyFactory(HsfService hsfService) {
			super(hsfService);
			syncDispatchStrategy = new SyncBroadcastDispatchStrategy(hsfService);
			asyncDispatchStrategy = new AsyncBroadcastDispatchStrategy(hsfService);
		}
	}
}
