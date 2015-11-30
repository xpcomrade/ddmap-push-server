package com.ddmap.push.test.listener;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import com.ddmap.push.netty.channel.HsfChannel;
import com.ddmap.push.netty.listener.ChannelEventListenerAdapter;
import com.ddmap.push.netty.listener.EventBehavior;
import com.ddmap.push.util.HsfContextHolder;

public class TestChannelListener extends ChannelEventListenerAdapter {

	@Override
	public EventBehavior groupRemoved(ChannelHandlerContext ctx, HsfChannel channel, String groupName) {
		//
		System.out.println(HsfContextHolder.getAttributes());
		System.out.println("group " + groupName + " is removed");
		return null;
	}

	@Override
	public EventBehavior groupCreated(ChannelHandlerContext ctx, HsfChannel channel, String groupName) {
		//
		HsfContextHolder.getAttributes().put("hello", "helsddsd");
		System.out.println("group " + groupName + " is created");
		return null;
	}

	@Override
	public EventBehavior channelConnected(ChannelHandlerContext ctx, HsfChannel channel, ChannelStateEvent e) {
		//
		System.out.println("channel " + channel + " is connected");
		return null;
	}

	@Override
	public EventBehavior channelClosed(ChannelHandlerContext ctx, HsfChannel channel, ChannelStateEvent e) {
		//

		System.out.println("channel " + channel + " is closed");
		return null;
	}
}
