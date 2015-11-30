package com.ddmap.push.netty.listener;

import java.util.EventListener;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;

import com.ddmap.push.netty.channel.HsfChannel;

/**
 * @Title: ChannelEventListener.java
 * @Package com.ddmap.push.netty.listener
 * @Description: 通道事件监听类
 * @author guo
 * @date 2011-9-27 上午11:45:50
 * @version V1.0
 */
public interface ChannelEventListener extends EventListener {

	/**
	 * Invoked when a {@link Channel} was closed and all its related resources were released.
	 * 
	 * @author guo
	 * @param ctx
	 * @param channel
	 * @param e
	 * @return EventBehavior Whether to continue the events deliver
	 */
	public EventBehavior channelClosed(ChannelHandlerContext ctx, HsfChannel channel, ChannelStateEvent e);

	/**
	 * Invoked when a {@link Channel} is open, bound to a local address, and connected to a remote address.
	 * 
	 * @author guo
	 * @param ctx
	 * @param channel
	 * @param e
	 * @return EventBehavior Whether to continue the events deliver
	 */
	public EventBehavior channelConnected(ChannelHandlerContext ctx, HsfChannel channel, ChannelStateEvent e);

	/**
	 * Invoked when a group is created.
	 * 
	 * @author guo
	 * @param ctx
	 * @param channel
	 * @param groupName
	 * @return EventBehavior Whether to continue the events deliver
	 */
	public EventBehavior groupCreated(ChannelHandlerContext ctx, HsfChannel channel, String groupName);

	/**
	 * Invoked when a group is removed.
	 * 
	 * @author guo
	 * @param ctx
	 * @param channel
	 * @param groupName
	 * @return EventBehavior Whether to continue the events deliver
	 */
	public EventBehavior groupRemoved(ChannelHandlerContext ctx, HsfChannel channel, String groupName);
}
