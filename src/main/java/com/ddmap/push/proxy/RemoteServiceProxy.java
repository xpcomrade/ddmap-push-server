package com.ddmap.push.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.apache.commons.lang.StringUtils;

import com.ddmap.push.netty.dispatcher.async.AsyncDispatchStrategy;
import com.ddmap.push.netty.dispatcher.async.AsyncRoundDispatchStrategy;
import com.ddmap.push.netty.dispatcher.sync.SyncDispatchStrategy;
import com.ddmap.push.netty.dispatcher.sync.SyncRoundDispatchStrategy;
import com.ddmap.push.netty.service.HsfService;
import com.ddmap.push.proxy.strategy.AsyncServiceRequestHandler;
import com.ddmap.push.proxy.strategy.AsyncSingleGroupRequestHandler;
import com.ddmap.push.proxy.strategy.SyncServiceRequestHandler;
import com.ddmap.push.proxy.strategy.SyncSingleGroupRequestHandler;
import com.ddmap.push.util.AsyncCallback;
import com.ddmap.push.util.AsyncType;

/**
 * @Title: RemoteServiceProxy.java
 * @Package com.ddmap.push.proxy
 * @Description: 为远程服务创建动态代理，使用该代理，可直接调用远程服务
 * @author guo
 * @date 2011-9-16 下午3:07:29
 * @version V1.0
 */
public class RemoteServiceProxy {

	/**
	 * @Title: wrapSyncProxy
	 * @Description: 为远程服务创建同步动态代理，返回代理对象，分发策略采取SyncRoundDispatchStrategy
	 * @author guo
	 * @param serviceInterface
	 *        远程服务接口
	 * @param service
	 *        通论服务
	 * @return T 返回类型
	 */
	public static <T> T wrapSyncProxy(Class<T> serviceInterface, HsfService service) {
		return wrapSyncProxy(serviceInterface, new SyncRoundDispatchStrategy(service));
	}

	/**
	 * @Title: wrapSyncProxy
	 * @Description: 为远程服务创建同步动态代理，返回代理对象
	 * @author guo
	 * @param serviceInterface
	 *        远程服务接口
	 * @param dispatchStrategy
	 *        分发策略
	 * @return T 返回类型
	 */
	public static <T> T wrapSyncProxy(Class<T> serviceInterface, SyncDispatchStrategy dispatchStrategy) {
		return wrapSyncProxy4Service(serviceInterface, dispatchStrategy);
	}

	/**
	 * @Title: wrapSyncProxy
	 * @Description: 为远程服务创建同步动态代理，返回代理对象
	 * @author guo
	 * @param serviceInterface
	 *        远程服务接口
	 * @param service
	 *        Hsf远程服务对象
	 * @param groupName
	 *        消息发送目的地Group名称
	 * @return T 返回类型
	 */
	@SuppressWarnings("unchecked")
	public static <T> T wrapSyncProxy(Class<T> serviceInterface, HsfService service, String groupName) {
		if (serviceInterface == null) {
			throw new IllegalArgumentException("serviceInterface can not be null.");
		} else if (!serviceInterface.isInterface()) {
			throw new IllegalArgumentException("serviceInterface is required to be interface.");
		} else if (service == null) {
			throw new IllegalArgumentException("service can not be null.");
		} else if (StringUtils.isBlank(groupName)) {
			throw new IllegalArgumentException("groupName can not be null or empty.");
		}

		InvocationHandler requestHandler = new SyncSingleGroupRequestHandler(serviceInterface.getSimpleName(), service,
				groupName);

		// 创建代理
		T serviceProxy = (T) Proxy.newProxyInstance(getClassLoader(serviceInterface), new Class[] { serviceInterface },
				requestHandler);

		return serviceProxy;
	}

	/**
	 * @Title: wrapAsyncFutureProxy
	 * @Description: 为远程服务创建异步Future动态代理，返回代理对象，分发策略采取AsyncRoundDispatchStrategy
	 * @author guo
	 * @param serviceInterface
	 *        远程服务接口
	 * @param service
	 *        通信服务
	 * @return T 返回类型
	 */
	public static <T> T wrapAsyncFutureProxy(Class<T> serviceInterface, HsfService service) {
		return wrapAsyncFutureProxy(serviceInterface, new AsyncRoundDispatchStrategy(service));
	}

	/**
	 * @Title: wrapAsyncFutureProxy
	 * @Description: 为远程服务创建异步Future动态代理，返回代理对象
	 * @author guo
	 * @param serviceInterface
	 *        远程服务接口
	 * @param service
	 *        通信服务
	 * @param groupName
	 *        消息发送目的地Group名称
	 * @return T 返回类型
	 */
	public static <T> T wrapAsyncFutureProxy(Class<T> serviceInterface, HsfService service, String groupName) {
		return wrapAsyncSingleProxy(service, serviceInterface, groupName, AsyncType.Future, null);
	}

	/**
	 * @Title: wrapAsyncFutureProxy
	 * @Description: 为远程服务创建异步Future动态代理，返回代理对象
	 * @author guo
	 * @param serviceInterface
	 *        远程服务接口
	 * @param dispatchStrategy
	 *        分发策略
	 * @return T 返回类型
	 */
	public static <T> T wrapAsyncFutureProxy(Class<T> serviceInterface, AsyncDispatchStrategy dispatchStrategy) {
		return wrapAsyncProxy4Service(serviceInterface, AsyncType.Future, null, dispatchStrategy);
	}

	/**
	 * @Title: wrapAsyncCallbackProxy
	 * @Description: 为远程服务创建异步回调动态代理，返回代理对象，分发策略采取AsyncRoundDispatchStrategy
	 * @author guo
	 * @param serviceInterface
	 *        远程服务接口
	 * @param callback
	 *        回调
	 * @param service
	 *        通信服务
	 * @return T 返回类型
	 */
	public static <T> T wrapAsyncCallbackProxy(Class<T> serviceInterface, AsyncCallback<?> callback, HsfService service) {
		return wrapAsyncCallbackProxy(serviceInterface, callback, new AsyncRoundDispatchStrategy(service));
	}

	/**
	 * @Title: wrapAsyncCallbackProxy
	 * @Description: 为远程服务创建异步回调动态代理，返回代理对象
	 * @author guo
	 * @param serviceInterface
	 *        远程服务接口
	 * @param callback
	 *        回调
	 * @param service
	 *        通信服务
	 * @param groupName
	 *        消息发送目的地Group名称
	 * @return T 返回类型
	 */
	public static <T> T wrapAsyncCallbackProxy(Class<T> serviceInterface, AsyncCallback<?> callback,
			HsfService service, String groupName) {
		return wrapAsyncSingleProxy(service, serviceInterface, groupName, AsyncType.Callback, callback);
	}

	/**
	 * @Title: wrapAsyncCallbackProxy
	 * @Description: 为远程服务创建异步回调动态代理，返回代理对象
	 * @author guo
	 * @param serviceInterface
	 *        远程服务接口
	 * @param callback
	 *        回调
	 * @param dispatchStrategy
	 *        分发策略
	 * @return T 返回类型
	 */
	public static <T> T wrapAsyncCallbackProxy(Class<T> serviceInterface, AsyncCallback<?> callback,
			AsyncDispatchStrategy dispatchStrategy) {
		return wrapAsyncProxy4Service(serviceInterface, AsyncType.Callback, callback, dispatchStrategy);
	}

	/**
	 * @Title: wrapSyncProxy4Service
	 * @Description: 为远程服务创建同步动态代理，返回代理对象
	 * @author guo
	 * @param serviceInterface
	 *        远程服务接口
	 * @param dispatchStrategy
	 *        分发策略
	 * @return T 返回类型
	 */
	@SuppressWarnings("unchecked")
	private static <T> T wrapSyncProxy4Service(Class<T> serviceInterface, SyncDispatchStrategy dispatchStrategy) {
		if (serviceInterface == null) {
			throw new IllegalArgumentException("serviceInterface can not be null.");
		} else if (!serviceInterface.isInterface()) {
			throw new IllegalArgumentException("serviceInterface is required to be interface.");
		} else if (dispatchStrategy == null) {
			throw new IllegalArgumentException("dispatchStrategy is required to be interface.");
		}

		InvocationHandler requestHandler = new SyncServiceRequestHandler(serviceInterface.getSimpleName(),
				dispatchStrategy);

		// 创建代理
		T serviceProxy = (T) Proxy.newProxyInstance(getClassLoader(serviceInterface), new Class[] { serviceInterface },
				requestHandler);

		return serviceProxy;
	}

	/**
	 * @Title: wrapAsyncProxy4Service
	 * @Description: 为远程服务创建异步动态代理，返回代理对象
	 * @author guo
	 * @param serviceInterface
	 *        远程服务接口
	 * @param asyncType
	 *        异步类型
	 * @param callback
	 *        回调
	 * @param dispatchStrategy
	 *        分发策略
	 * @return T 返回类型
	 */
	@SuppressWarnings("unchecked")
	private static <T> T wrapAsyncProxy4Service(Class<T> serviceInterface, AsyncType asyncType,
			AsyncCallback<?> callback, AsyncDispatchStrategy dispatchStrategy) {
		if (serviceInterface == null) {
			throw new IllegalArgumentException("serviceInterface can not be null.");
		} else if (!serviceInterface.isInterface()) {
			throw new IllegalArgumentException("serviceInterface is required to be interface.");
		} else if (dispatchStrategy == null) {
			throw new IllegalArgumentException("dispatchStrategy can not be null.");
		}

		InvocationHandler requestHandler = new AsyncServiceRequestHandler(serviceInterface.getSimpleName(), asyncType,
				callback, dispatchStrategy);

		// 创建代理
		T serviceProxy = (T) Proxy.newProxyInstance(getClassLoader(serviceInterface), new Class[] { serviceInterface },
				requestHandler);

		return serviceProxy;
	}

	/**
	 * @Title: wrapAsyncSingleProxy
	 * @Description: 为远程服务创建异步动态代理，返回代理对象
	 * @author guo
	 * @param service
	 * @param serviceInterface
	 * @param groupName
	 * @param asyncType
	 * @param callback
	 * @return T 返回类型
	 */
	@SuppressWarnings("unchecked")
	private static <T> T wrapAsyncSingleProxy(HsfService service, Class<T> serviceInterface, String groupName,
			AsyncType asyncType, AsyncCallback<?> callback) {
		if (serviceInterface == null) {
			throw new IllegalArgumentException("serviceInterface can not be null.");
		} else if (!serviceInterface.isInterface()) {
			throw new IllegalArgumentException("serviceInterface is required to be interface.");
		} else if (groupName == null) {
			throw new IllegalArgumentException("groupName can not be null.");
		}

		InvocationHandler requestHandler = new AsyncSingleGroupRequestHandler(service,
				serviceInterface.getSimpleName(), asyncType, callback, groupName);

		// 创建代理
		T serviceProxy = (T) Proxy.newProxyInstance(getClassLoader(serviceInterface), new Class[] { serviceInterface },
				requestHandler);

		return serviceProxy;
	}

	private static ClassLoader getClassLoader(Class<?> clazz) {
		if (clazz != null && clazz.getClassLoader() != null) {
			return clazz.getClassLoader();
		}

		if (Thread.currentThread().getContextClassLoader() != null) {
			return Thread.currentThread().getContextClassLoader();
		}

		return ClassLoader.getSystemClassLoader();
	}
}
