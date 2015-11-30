package com.ddmap.push.netty.listener.impl;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.DefaultExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ddmap.push.netty.channel.HsfChannel;
import com.ddmap.push.netty.channel.HsfChannelGroup;
import com.ddmap.push.netty.channel.RoundChannelGroup;
import com.ddmap.push.netty.event.EventDispatcher;
import com.ddmap.push.netty.handshake.AcceptorHandshakeProcessor;
import com.ddmap.push.netty.listener.ChannelEventListenerAdapter;
import com.ddmap.push.netty.listener.EventBehavior;
import com.ddmap.push.netty.listener.MessageEventListener;
import com.ddmap.push.netty.service.HsfAcceptor;
import com.ddmap.push.netty.service.HsfService;
import com.ddmap.push.pojo.HandshakeAck;
import com.ddmap.push.pojo.HandshakeFinish;
import com.ddmap.push.pojo.HandshakeRequest;
import com.ddmap.push.pojo.PushRequest;
import com.ddmap.push.redis.OmitMessage;
import com.ddmap.push.redis.PhoneInfo;
import com.ddmap.push.util.HandshakeUtil;
import com.ddmap.push.util.IPUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author guo
 * @version V1.0
 * @Title: AcceptorGroupMessageEventListener.java
 * @Package com.ddmap.push.netty.listener
 * @Description: 服务端握手消息监控类
 * @date 2012-2-22 上午08:33:52
 */
public class AcceptorGroupMessageEventListener extends ChannelEventListenerAdapter implements MessageEventListener {
    private static String ip = IPUtils.getInnerIp();
    private Logger logger = LoggerFactory.getLogger(getClass());
    private EventDispatcher eventDispatcher;

    public AcceptorGroupMessageEventListener(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public EventBehavior channelConnected(ChannelHandlerContext ctx, HsfChannel channel, ChannelStateEvent e) {
        // start timer
        HandshakeUtil.resetHandshakeTimeout(channel);

        return EventBehavior.Continue;
    }
    @Override
    public EventBehavior channelClosed(ChannelHandlerContext ctx, HsfChannel channel, ChannelStateEvent e) {
        String groupName = channel.getChannelGroup().getName();
        String channelId = String.valueOf(e.getChannel().getId());
        PhoneInfo.disConnInfo(groupName, channelId);
        return EventBehavior.Continue;
    }
    @Override
    public EventBehavior messageReceived(ChannelHandlerContext ctx, HsfChannel channel, MessageEvent e) {
        //
        if (e.getMessage() instanceof HandshakeRequest) {
            processRequest(ctx, channel, (HandshakeRequest) e.getMessage());
        } else if (e.getMessage() instanceof HandshakeFinish) {
            processFinish(ctx, channel, (HandshakeFinish) e.getMessage());
        }

        return EventBehavior.Continue;
    }

    private void processRequest(ChannelHandlerContext ctx, HsfChannel channel, HandshakeRequest message) {
        // cancel timeout
        HandshakeUtil.cancelHandshakeTimeout(channel);

        logger.info("received handshake request({}) from channel({})", message, channel);
        //
        String groupName = message.getGroupName();
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

        AcceptorHandshakeProcessor handshakeProcessor = ((HsfAcceptor) eventDispatcher.getService())
                .getHandshakeProcessor();
        // dispatch group created event
        if (isCreate) {
            if (handshakeProcessor != null) {
                try {
                    Map<String, Object> initAttrMap = handshakeProcessor.getInitAttributes();
                    if (initAttrMap != null) {
                        channelGroup.getAttributes().putAll(initAttrMap);
                    }
                    //
                    handshakeProcessor.process(message);
                } catch (Exception ex) {
                    eventDispatcher.dispatchExceptionCaught(ctx, channel, new DefaultExceptionEvent(channel, ex));
                }
            }
            //
            eventDispatcher.dispatchGroupCreatedEvent(ctx, channel, groupName);
        }
        // write handshake ack
        HandshakeAck ack = new HandshakeAck(groupName);
        if (handshakeProcessor != null) {
            ack.setAttachment(handshakeProcessor.getAckAttachment());
        }
        //
        logger.info("send handshake ack({}) to channel({})", ack, channel);
        // start timer
        HandshakeUtil.resetHandshakeTimeout(channel);
        //
        channel.write(ack);
    }


    private void processFinish(ChannelHandlerContext ctx, HsfChannel channel, HandshakeFinish message) {
        // cancel timeout
        HandshakeUtil.cancelHandshakeTimeout(channel);

        logger.info("received handshake finish({}) from channel({})", message, channel);
        //
        String groupName = message.getGroupName();
        HsfService service = eventDispatcher.getService();

        //
        HsfChannelGroup channelGroup = service.getGroups().get(groupName);
        if (channelGroup == null) {
            logger.warn("received {} from channel {} but group is not created. force to close channel", message,
                    channel);
            channel.close();
            return;
        }
        //
        if (channelGroup.setPrepared(true)) {
            AcceptorHandshakeProcessor handshakeProcessor = ((HsfAcceptor) eventDispatcher.getService())
                    .getHandshakeProcessor();
            if (handshakeProcessor != null) {
                try {
                    handshakeProcessor.process(message);
                } catch (Exception ex) {
                    eventDispatcher.dispatchExceptionCaught(ctx, channel, new DefaultExceptionEvent(channel, ex));
                }
            }
        }

        //将连接信息存入redis
        PhoneInfo.setConnInfo(groupName, channel.getId().toString(), ip);
        //当链接握手完成之后，做一次是否有未取消息的查询
        Set<String> set = OmitMessage.getPushInfo(groupName);
        HashMap<String, String> pushMap = new HashMap<String, String>();
        for (Iterator<String> iterator = set.iterator(); iterator.hasNext(); ) {
            String messageId = iterator.next();
            String messageInfo = OmitMessage.getMessageInfo(messageId);
            pushMap.put(messageId, messageInfo);
        }
        if (pushMap.size() > 0) {
            PushRequest pushRequest = new PushRequest(pushMap);
            channel.write(pushRequest);
        }
    }
}
