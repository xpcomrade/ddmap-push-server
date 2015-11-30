package com.ddmap.push.netty.dispatcher.sync;

import java.util.Map.Entry;

import com.ddmap.push.exception.HsfOperationException;
import com.ddmap.push.netty.channel.HsfChannel;
import com.ddmap.push.netty.channel.HsfChannelGroup;
import com.ddmap.push.netty.dispatcher.InvokeResult;
import com.ddmap.push.netty.service.HsfService;

/**
 * @Title: SyncBroadcastDispatchStrategy.java
 * @Package com.ddmap.push.netty.dispatcher.sync
 * @Description: 同步广播式分发策略实现
 * @author guo
 * @date 2011-9-29 下午3:20:53
 * @version V1.0
 */
public class SyncBroadcastDispatchStrategy extends SyncAbstractDispatchStrategy {

	public SyncBroadcastDispatchStrategy(HsfService service) {
		super(service);
	}

	@Override
	public InvokeResult dispatch(Object message) {
		if (message == null) {
			throw new IllegalArgumentException("Message can not be null.");
		} else if (!service.isAlived()) {
			throw new IllegalStateException("service is not alived.");
		}

		InvokeResult invokeResult = new InvokeResult();

		for (Entry<String, HsfChannelGroup> entry : service.getGroups().entrySet()) {
			HsfChannelGroup group = entry.getValue();
			HsfChannel channel = group.getNextChannel();

			if (channel != null) {
				try {
					Object retObj = channel.writeSync(message);
					invokeResult.put(group.getName(), retObj);
				} catch (Exception e) {
					invokeResult.put(channel, e);
				}
			} else {
				invokeResult.put(channel, new HsfOperationException("HsfService Channel can not be null."));
			}
		}

		return invokeResult;
	}

}
