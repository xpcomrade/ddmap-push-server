package com.ddmap.push.proxy.strategy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Set;

import com.ddmap.push.future.InvokeFuture;
import com.ddmap.push.future.MultiInvokeFuture;
import com.ddmap.push.netty.dispatcher.InvokeResult;
import com.ddmap.push.netty.dispatcher.async.AsyncDispatchStrategy;
import com.ddmap.push.pojo.RemoteServiceObject;
import com.ddmap.push.util.AsyncCallback;
import com.ddmap.push.util.AsyncType;
import com.ddmap.push.util.ReflectionUtil;
import com.ddmap.push.util.TLSUtil;

/**
 * @Title: AsyncServiceRequestHandler.java
 * @Package com.ddmap.push.proxy.strategy
 * @Description: 异步请求处理
 * @author guo
 * @date 2011-9-30 下午3:11:08
 * @version V1.0
 */
@SuppressWarnings("rawtypes")
public class AsyncServiceRequestHandler implements InvocationHandler {
	String serviceName;
	AsyncType asyncType;
	AsyncCallback callback;
	AsyncDispatchStrategy dispatchStrategy;

	public AsyncServiceRequestHandler(String serviceName, AsyncType asyncType, AsyncCallback callback,
			AsyncDispatchStrategy dispatchStrategy) {
		if (serviceName == null) {
			throw new IllegalArgumentException("serviceName can not be null.");
		} else if (dispatchStrategy == null) {
			throw new IllegalArgumentException("dispatchStrategy can not be null.");
		}

		this.serviceName = serviceName;
		this.asyncType = asyncType == null ? AsyncType.Default : asyncType;
		this.callback = callback;
		this.dispatchStrategy = dispatchStrategy;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		RemoteServiceObject remoteServiceObject = new RemoteServiceObject();
		remoteServiceObject.setMethodName(method.getName());
		remoteServiceObject.setServiceName(serviceName);
		remoteServiceObject.setArgs(args);

		InvokeResult result;

		if (AsyncType.Callback.equals(asyncType)) {
			result = dispatchStrategy.dispatch(remoteServiceObject, callback);
		} else {
			result = dispatchStrategy.dispatch(remoteServiceObject, asyncType);
		}

		if (result != null && result.size() == 1) {
			Object retObj = result.getFirstValue();

			TLSUtil.setData(InvokeFuture.class, retObj);
		} else if (AsyncType.Future.equals(asyncType)) {
			MultiInvokeFuture multiInvokeFuture = new MultiInvokeFuture();
			Set<Object> keySet = result.keySet();

			for (Object groupName : keySet) {
				multiInvokeFuture.addFuture((InvokeFuture) result.get(groupName));
			}

			TLSUtil.setData(InvokeFuture.class, multiInvokeFuture);
		}

		return ReflectionUtil.getDefaultValue(method.getReturnType());
	}
}