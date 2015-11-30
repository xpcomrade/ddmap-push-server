package com.ddmap.push.proxy.strategy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.ddmap.push.exception.HsfOperationException;
import com.ddmap.push.netty.channel.HsfChannel;
import com.ddmap.push.netty.channel.HsfChannelGroup;
import com.ddmap.push.netty.service.HsfService;
import com.ddmap.push.pojo.RemoteServiceObject;

/**
 * @Title: SyncSingleGroupRequestHandler.java
 * @Package com.ddmap.push.proxy.strategy
 * @Description: 单Group同步请求处理
 * @author guo
 * @date 2011-9-30 下午3:10:10
 * @version V1.0
 */
public class SyncSingleGroupRequestHandler implements InvocationHandler {
	String serviceName;
	HsfService service;
	String groupName;

	public SyncSingleGroupRequestHandler(String serviceName, HsfService service, String groupName) {
		if (serviceName == null) {
			throw new IllegalArgumentException("serviceName can not be null.");
		} else if (service == null) {
			throw new IllegalArgumentException("service can not be null.");
		} else if (groupName == null) {
			throw new IllegalArgumentException("groupName can not be null.");
		}

		this.serviceName = serviceName;
		this.service = service;
		this.groupName = groupName;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (!service.isAlived()) {
			throw new IllegalStateException("service is not alived.");
		}

		RemoteServiceObject remoteServiceObject = new RemoteServiceObject();
		remoteServiceObject.setMethodName(method.getName());
		remoteServiceObject.setServiceName(serviceName);
		remoteServiceObject.setArgs(args);

		// 向Group发送消息
		return writeSync(remoteServiceObject, groupName);
	}

	public Object writeSync(Object message, String groupName) {
		HsfChannelGroup group = service.getGroups().get(groupName);
		if (group == null) {
			throw new HsfOperationException("HsfService group(" + groupName + ") is not existed.");
		}

		// 获取Channel，发送消息
		HsfChannel channel = group.getNextChannel();

		return channel.writeSync(message);
	}
}