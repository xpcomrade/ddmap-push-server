package com.ddmap.push.netty.dispatcher.sync;

import java.util.concurrent.atomic.AtomicLong;

import com.ddmap.push.netty.channel.HsfChannel;
import com.ddmap.push.netty.channel.HsfChannelGroup;
import com.ddmap.push.netty.dispatcher.InvokeResult;
import com.ddmap.push.netty.service.HsfService;
import com.ddmap.push.util.ConcurrentArrayListHashMap;
import com.ddmap.push.util.HsfContextHolder;
import com.ddmap.push.util.StackTraceUtil;

/**
 * @Title: SyncRoundDispatchStrategy.java
 * @Package com.ddmap.push.netty.dispatcher.sync
 * @Description: 同步环式分发策略实现
 * @author guo
 * @date 2011-9-29 下午2:56:05
 * @version V1.0
 */
public class SyncRoundDispatchStrategy extends SyncAbstractDispatchStrategy {

	private static final AtomicLong STATIC_SEQ = new AtomicLong(0);
	private AtomicLong groupIndex;

	public SyncRoundDispatchStrategy(HsfService service) {
		super(service);
		groupIndex = new AtomicLong(STATIC_SEQ.getAndIncrement());
	}

	@Override
	public InvokeResult dispatch(Object message) {
		if (message == null) {
			throw new IllegalArgumentException("Message can not be null.");
		} else if (!service.isAlived()) {
			throw new IllegalStateException("service is not alived.");
		}

		HsfChannel channel = getChannel(service.getGroups());
		Object retObj = write(message, channel);

		// 构建结果
		InvokeResult invokeResult = new InvokeResult();
		invokeResult.put(((HsfChannel) channel).getChannelGroup().getName(), retObj);

		return invokeResult;
	}

	/**
	 * 
	 * @Title: getChannel
	 * @Description: 获取一个有效通道
	 * @author guo
	 * @param groups
	 * @return HsfChannel 返回类型
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
