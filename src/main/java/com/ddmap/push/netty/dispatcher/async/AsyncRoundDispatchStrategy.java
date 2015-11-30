package com.ddmap.push.netty.dispatcher.async;

import java.util.concurrent.atomic.AtomicLong;

import com.ddmap.push.netty.channel.HsfChannel;
import com.ddmap.push.netty.channel.HsfChannelGroup;
import com.ddmap.push.netty.dispatcher.InvokeResult;
import com.ddmap.push.netty.service.HsfService;
import com.ddmap.push.util.AsyncCallback;
import com.ddmap.push.util.AsyncType;
import com.ddmap.push.util.ConcurrentArrayListHashMap;
import com.ddmap.push.util.HsfContextHolder;
import com.ddmap.push.util.StackTraceUtil;

/**
 * @Title: AsyncRoundDispatchStrategy.java
 * @Package com.ddmap.push.netty.dispatcher.async
 * @Description: 异步环式分发策略实现
 * @author guo
 * @date 2011-9-29 下午2:16:17
 * @version V1.0
 */
public class AsyncRoundDispatchStrategy extends AsyncAbstractDispatchStrategy {

	private static final AtomicLong STATIC_SEQ = new AtomicLong(0);
	private AtomicLong groupIndex;

	public AsyncRoundDispatchStrategy(HsfService service) {
		super(service);
		groupIndex = new AtomicLong(STATIC_SEQ.getAndIncrement());
	}

	@Override
	public InvokeResult dispatch(Object message, AsyncType asyncType) {
		if (message == null) {
			throw new IllegalArgumentException("Message can not be null.");
		} else if (!service.isAlived()) {
			throw new IllegalStateException("service is not alived.");
		} else if (AsyncType.Callback.equals(asyncType)) {
			throw new IllegalArgumentException("only support AsyncType.None and AsyncType.Future.");
		}

		// 默认为None
		if (asyncType == null) {
			asyncType = AsyncType.Default;
		}

		InvokeResult invokeResult = new InvokeResult();
		HsfChannel channel = getChannel(service.getGroups());

		switch (asyncType) {
		case Default:
			invokeResult.put(((HsfChannel) channel).getChannelGroup().getName(), channel.write(message));
			break;

		case Future:
			invokeResult.put(channel.getChannelGroup().getName(), channel.writeAsync(message));
			break;
		}

		return invokeResult;
	}

	@Override
	public InvokeResult dispatch(Object message, AsyncCallback<?> callback) {
		if (message == null) {
			throw new IllegalArgumentException("Message can not be null.");
		} else if (!service.isAlived()) {
			throw new IllegalStateException("service is not alived.");
		}

		InvokeResult invokeResult = new InvokeResult();
		HsfChannel channel = getChannel(service.getGroups());

		// 发送消息
		channel.writeAsync(message, callback);

		return invokeResult;
	}

	/**
	 * 
	 * @Title: getChannel
	 * @Description: 获取一个有效通道
	 * @author guo
	 * @param groups
	 * @return Channel 返回类型
	 */
	private HsfChannel getChannel(ConcurrentArrayListHashMap<String, HsfChannelGroup> groups) {
		int groupSize = 0;
		int groupCount = 0;
		long index;
		HsfChannel channel;
		Object[] groupArray;
		HsfChannelGroup hsfChannelGroup;
		//
		groupArray = groups.arrayValues();
		index = groupIndex.getAndIncrement();
		groupSize = groupArray.length;

		while (true) {
			groupCount++;

			groupArray = groups.arrayValues();
			int size = groupArray.length;
			if (size == 0) {
				return null;
			}

			int position = (int) (index++ % size);
			try {
				hsfChannelGroup = (HsfChannelGroup) groupArray[position];
				// 未准备好（还没处理完GroupCreated事件）
				if (!hsfChannelGroup.isPrepared() && !HsfContextHolder.isInProcessingGroupCreatedEvent()
						|| hsfChannelGroup.isClosed()) {
					continue;
				}

				channel = hsfChannelGroup.getNextChannel();
				return channel;
			} catch (IndexOutOfBoundsException e) {
				logger.warn(StackTraceUtil.getStackTrace(e));
			}

			if (groupCount >= groupSize * 2) {
				return null;
			}
		}
	}
}
