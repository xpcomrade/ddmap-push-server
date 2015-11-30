package com.ddmap.push.configserver.listener;

import org.jboss.netty.channel.ChannelHandlerContext;
import com.ddmap.push.configserver.ConfigManager;
import com.ddmap.push.netty.channel.HsfChannel;
import com.ddmap.push.netty.listener.ChannelEventListenerAdapter;
import com.ddmap.push.netty.listener.EventBehavior;

/**
 * @Title: ConfigServerChannelListener.java
 * @Package com.ddmap.push.configserver.listener
 * @Description: 
 * @author guo
 * @date 2012-3-22 下午3:37:54
 * @version V1.0
 */
public class ConfigServerChannelListener extends ChannelEventListenerAdapter {
	@Override
	public EventBehavior groupRemoved(ChannelHandlerContext ctx, HsfChannel channel, String groupName) {
		// 解除所有订阅
		ConfigManager configManager = ConfigManager.getConfigManager();
		configManager.unsubscribe(groupName);

		return EventBehavior.Continue;
	}
}
