package com.ddmap.push.proxy.strategy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.ddmap.push.netty.dispatcher.InvokeResult;
import com.ddmap.push.netty.dispatcher.sync.SyncDispatchStrategy;
import com.ddmap.push.pojo.RemoteServiceObject;
import com.ddmap.push.util.ReflectionUtil;

/**
 * @Title: SyncServiceRequestHandler.java
 * @Package com.ddmap.push.proxy.strategy
 * @Description: 同步请求处理
 * @author guo
 * @date 2011-9-30 下午3:10:10
 * @version V1.0
 */
public class SyncServiceRequestHandler implements InvocationHandler {
	String serviceName;
	SyncDispatchStrategy dispatchStrategy;

	public SyncServiceRequestHandler(String serviceName, SyncDispatchStrategy dispatchStrategy) {
		if (serviceName == null) {
			throw new IllegalArgumentException("serviceName can not be null.");
		} else if (dispatchStrategy == null) {
			throw new IllegalArgumentException("dispatchStrategy can not be null.");
		}

		this.serviceName = serviceName;
		this.dispatchStrategy = dispatchStrategy;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		RemoteServiceObject remoteServiceObject = new RemoteServiceObject();
		remoteServiceObject.setMethodName(method.getName());
		remoteServiceObject.setServiceName(serviceName);
		remoteServiceObject.setArgs(args);

		InvokeResult result = dispatchStrategy.dispatch(remoteServiceObject);

		if (result.size() > 0) {
			return result.getFirstValue();
		}

		return ReflectionUtil.getDefaultValue(method.getReturnType());
	}
}