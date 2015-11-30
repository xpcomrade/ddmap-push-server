package com.ddmap.push.netty.dispatcher.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ddmap.push.exception.HsfOperationException;
import com.ddmap.push.netty.channel.HsfChannel;
import com.ddmap.push.netty.service.HsfService;

/**
 * @Title: SyncAbstractDispatchStrategy.java
 * @Package com.ddmap.push.netty.dispatcher.sync
 * @Description: 同步分发策略抽象
 * @author guo
 * @date 2011-9-29 下午2:32:23
 * @version V1.0
 */
public abstract class SyncAbstractDispatchStrategy implements SyncDispatchStrategy {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected HsfService service;

	public SyncAbstractDispatchStrategy(HsfService service) {
		if (service == null) {
			throw new IllegalArgumentException("HsfService can not be null.");
		}
		this.service = service;
	}

	@Override
	public HsfService getService() {
		return service;
	}

	protected Object write(Object message, HsfChannel channel) {
		if (channel == null) {
			throw new HsfOperationException("HsfService Channel can not be null.");
		}

		return channel.writeSync(message);
	}
}
