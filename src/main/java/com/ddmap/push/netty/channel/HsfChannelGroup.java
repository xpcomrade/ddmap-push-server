package com.ddmap.push.netty.channel;

import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import com.ddmap.push.statistic.ServiceStatisticInfo;
import com.ddmap.push.statistic.StatisticInfo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Title: HsfChannelGroup.java
 * @Package com.ddmap.push.netty.channel
 * @Description: Hsf ChannelGroup接口定义
 * @author guo
 * @date 2011-11-17 上午10:46:48
 * @version V1.0
 */
public interface HsfChannelGroup extends ChannelGroup {
	HsfChannel getNextChannel();

	List<HsfChannel> getChannels();

	Map<String, Object> getAttributes();

	ChannelGroupFuture close(boolean stopReconnect);

	boolean isPrepared();

	boolean setPrepared(boolean prepared);

	boolean isClosed();

	/**
	 * @Title: getCreateTime
	 * @Description: 获取Group创建时间
	 * @author guo
	 * @return Date 返回类型
	 */
	Date getCreateTime();

	StatisticInfo getMsgStatistic();

	ServiceStatisticInfo getServiceStatistic();
}
