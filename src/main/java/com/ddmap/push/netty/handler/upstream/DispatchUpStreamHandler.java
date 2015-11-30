package com.ddmap.push.netty.handler.upstream;

import com.ddmap.push.future.ChannelGroupFuture;
import com.ddmap.push.future.ChannelGroupFutureHolder;
import com.ddmap.push.netty.channel.HsfChannel;
import com.ddmap.push.netty.channel.HsfChannelGroup;
import com.ddmap.push.netty.event.EventDispatcher;
import org.jboss.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @Title: DispatchUpStreamHandler.java
 * @Package com.ddmap.push.netty.channelhandler.upstream
 * @Description: 事件分发Handler
 * @author guo
 * @date 2011-9-27 下午2:55:29
 * @version V1.0
 */
public class DispatchUpStreamHandler extends SimpleChannelUpstreamHandler {
	private Logger logger = LoggerFactory.getLogger(getClass());
	/**
	 * @Fields eventDispatcher : 事件分发类
	 */
	private EventDispatcher eventDispatcher;

	public DispatchUpStreamHandler(EventDispatcher eventDispatcher) {
		if (eventDispatcher == null) {
			throw new IllegalArgumentException("eventDispatcher");
		}

		this.eventDispatcher = eventDispatcher;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		logger.debug("hsf received msg:{} from {}", e.getMessage(), e.getChannel());
		HsfChannel hsfChannel = eventDispatcher.getService().getChannels().get(e.getChannel().getId());
		eventDispatcher.dispatchMessageEvent(ctx, hsfChannel, e);

		super.messageReceived(ctx, e);
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		logger.info("channel {} is connected.", e.getChannel());
		//
		HsfChannel hsfChannel = new HsfChannel(eventDispatcher.getService(), e.getChannel());
		ChannelGroupFuture channelFuture = ChannelGroupFutureHolder.remove(hsfChannel.getId());
		hsfChannel.setChannelGroupFuture(channelFuture);
		//
		eventDispatcher.getService().getChannels().put(hsfChannel.getId(), hsfChannel);
		eventDispatcher.dispatchChannelEvent(ctx, hsfChannel, e);

		super.channelConnected(ctx, e);
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		closeChannel(ctx, e);
		super.channelClosed(ctx, e);
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		closeChannel(ctx, e);
		super.channelDisconnected(ctx, e);
	}

	private void closeChannel(ChannelHandlerContext ctx, ChannelStateEvent e) {
		HsfChannel hsfChannel = eventDispatcher.getService().getChannels().remove(e.getChannel().getId());
		if (hsfChannel != null) {
			logger.info("channel {} is closed.", hsfChannel);
			//
			HsfChannelGroup channelGroup = hsfChannel.getChannelGroup();
			if (channelGroup != null) {
				channelGroup.remove(hsfChannel);
			}
			eventDispatcher.dispatchChannelEvent(ctx, hsfChannel, e);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		HsfChannel hsfChannel = eventDispatcher.getService().getChannels().get(e.getChannel().getId());
		if (hsfChannel != null) {
			eventDispatcher.dispatchExceptionCaught(ctx, hsfChannel, e);
		}
		// 处理IOException，主动关闭channel
		if (e.getCause() != null && e.getCause() instanceof IOException) {
			e.getChannel().close();
			closeChannel(ctx, new UpstreamChannelStateEvent(e.getChannel(), ChannelState.CONNECTED, null));
		}
	}
}
