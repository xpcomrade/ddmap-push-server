package com.ddmap.push.netty.dispatcher.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ddmap.push.netty.service.HsfService;

/**
 * @Title: AsyncAbstractDispatchStrategy.java
 * @Package com.ddmap.push.netty.dispatcher.async
 * @Description: 异步分发策略抽象
 * @author guo
 * @date 2011-9-29 下午2:32:23
 * @version V1.0
 */
public abstract class AsyncAbstractDispatchStrategy implements AsyncDispatchStrategy {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected HsfService service;

	public AsyncAbstractDispatchStrategy(HsfService service) {
		this.service = service;
	}

	@Override
	public HsfService getService() {
		return service;
	}
}
