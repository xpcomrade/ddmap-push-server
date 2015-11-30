package com.ddmap.push.netty.listener;

import java.util.EventListener;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import com.ddmap.push.netty.channel.HsfChannel;

/**
 * @Title: MessageListener.java
 * @Package com.ddmap.push.netty.listener
 * @Description: 消息监听接口
 * @author guo
 * @date 2011-9-27 上午11:36:22
 * @version V1.0
 */
public interface MessageEventListener extends EventListener {
    /**
     * Invoked when a message object (e.g: {@link ChannelBuffer}) was received
     * from a remote peer.
     */
	public EventBehavior messageReceived(ChannelHandlerContext ctx, HsfChannel channel, MessageEvent e);
}
