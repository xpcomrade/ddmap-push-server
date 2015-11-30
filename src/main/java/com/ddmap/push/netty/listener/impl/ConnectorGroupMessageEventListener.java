package com.ddmap.push.netty.listener.impl;

import com.ddmap.push.netty.channel.HsfChannel;
import com.ddmap.push.netty.channel.HsfChannelGroup;
import com.ddmap.push.netty.channel.RoundChannelGroup;
import com.ddmap.push.netty.event.EventDispatcher;
import com.ddmap.push.netty.handshake.ConnectorHandshakeProcessor;
import com.ddmap.push.netty.listener.ChannelEventListenerAdapter;
import com.ddmap.push.netty.listener.EventBehavior;
import com.ddmap.push.netty.listener.MessageEventListener;
import com.ddmap.push.netty.service.HsfConnector;
import com.ddmap.push.netty.service.HsfService;
import com.ddmap.push.pojo.HandshakeAck;
import com.ddmap.push.pojo.HandshakeFinish;
import com.ddmap.push.pojo.HandshakeRequest;
import com.ddmap.push.util.HandshakeUtil;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.DefaultExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 客户端握手消息监听类
 * 
 * @Title: ConnectorGroupMessageEventListener.java
 * @Package com.ddmap.push.netty.listener
 * @author guo
 * @date 2012-2-22 上午11:00:48
 * @version V1.0
 */
public class ConnectorGroupMessageEventListener extends ChannelEventListenerAdapter implements MessageEventListener {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private EventDispatcher eventDispatcher;

	public ConnectorGroupMessageEventListener(EventDispatcher eventDispatcher) {
		this.eventDispatcher = eventDispatcher;
	}

	@Override
	public EventBehavior channelConnected(ChannelHandlerContext ctx, HsfChannel channel, ChannelStateEvent e) {
		// send handshake request
		HandshakeRequest request = new HandshakeRequest(eventDispatcher.getService().getGroupName());
		ConnectorHandshakeProcessor handshakeProcessor = ((HsfConnector) eventDispatcher.getService())
				.getHandshakeProcessor();
		if (handshakeProcessor != null) {
			request.setAttachment(handshakeProcessor.getRequestAttachment());
		}

		//
		logger.info("send handshake request({}) to channel({})", request, channel);
		// start timer
		HandshakeUtil.resetHandshakeTimeout(channel);
		//
		channel.write(request);

		return super.channelConnected(ctx, channel, e);
	}

	@Override
	public EventBehavior messageReceived(ChannelHandlerContext ctx, HsfChannel channel, MessageEvent e) {
		//
		if (e.getMessage() instanceof HandshakeAck) {
			// cancel timeout
			HandshakeUtil.cancelHandshakeTimeout(channel);

			//
			HandshakeAck ack = (HandshakeAck) e.getMessage();
			logger.info("received handshake ack({}) from channel({})", ack, channel);

			//
			String groupName = ack.getGroupName();
			HsfService service = eventDispatcher.getService();
			//
			boolean isCreate = false;
			HsfChannelGroup channelGroup = service.getGroups().get(groupName);
			if (channelGroup == null) {
				channelGroup = new RoundChannelGroup(groupName);
				HsfChannelGroup preGroup = service.getGroups().putIfAbsent(groupName, channelGroup);
				if (!(isCreate = (preGroup == null))) {
					channelGroup = preGroup;
				}
			}

			// add channel to group
			channelGroup.add(channel);

			ConnectorHandshakeProcessor handshakeProcessor = ((HsfConnector) eventDispatcher.getService())
					.getHandshakeProcessor();

			// dispatch group created event
			if (isCreate) {
				//
				if (handshakeProcessor != null) {
					try {
						Map<String, Object> initAttrMap = handshakeProcessor.getInitAttributes();
						if (initAttrMap != null) {
							channelGroup.getAttributes().putAll(initAttrMap);
						}
						//
						handshakeProcessor.process(ack);
					} catch (Exception ex) {
						eventDispatcher.dispatchExceptionCaught(ctx, channel, new DefaultExceptionEvent(channel, ex));
					}
				}
				//
				eventDispatcher.dispatchGroupCreatedEvent(ctx, channel, groupName);
				//
				channelGroup.setPrepared(true);
			}
			// write handshake finish
			HandshakeFinish finish = new HandshakeFinish(service.getGroupName());
			if (handshakeProcessor != null) {
				finish.setAttachment(handshakeProcessor.getFinishAttachment());
			}
			channel.write(finish);
			//
			logger.info("send handshake finish({}) to channel({})", finish, channel);
		}

		return EventBehavior.Continue;
	}
}
