package com.ddmap.push.netty.listener;

import com.ddmap.push.netty.channel.HsfChannel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;

/**
 * @author guo
 * @version V1.0
 * @Title: ChannelEventListenerAdapter.java
 * @Package com.ddmap.push.netty.listener
 * @Description: ChannelEventListener适配类
 * @date 2011-9-29 上午11:08:05
 */
public abstract class ChannelEventListenerAdapter implements ChannelEventListener {

    @Override
    public EventBehavior channelClosed(ChannelHandlerContext ctx, HsfChannel channel, ChannelStateEvent e) {
        return EventBehavior.Continue;
    }

    @Override
    public EventBehavior channelConnected(ChannelHandlerContext ctx, HsfChannel channel, ChannelStateEvent e) {
        return EventBehavior.Continue;
    }

    @Override
    public EventBehavior groupCreated(ChannelHandlerContext ctx, HsfChannel channel, String groupName) {
        return EventBehavior.Continue;
    }

    @Override
    public EventBehavior groupRemoved(ChannelHandlerContext ctx, HsfChannel channel, String groupName) {
        return EventBehavior.Continue;
    }

}
