package com.ddmap.push.netty.handler;

import com.ddmap.push.netty.channel.HsfChannel;
import com.ddmap.push.netty.channel.HsfChannelGroup;
import com.ddmap.push.netty.service.HsfService;
import com.ddmap.push.pojo.Heartbeat;
import com.ddmap.push.pojo.PushRequest;
import com.ddmap.push.redis.OmitMessage;
import com.ddmap.push.redis.PhoneInfo;
import com.ddmap.push.statistic.HeartbeatStatisticInfo;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.DefaultExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author guo
 * @version V1.0
 * @Title: StateCheckChannelHandler.java
 * @Package com.ddmap.push.netty.channelhandler
 * @Description: 通道状态检测
 * @date 2011-9-17 上午10:12:13
 */
public class StateCheckChannelHandler extends IdleStateAwareChannelHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private HsfService hsfService;

    public StateCheckChannelHandler(HsfService hsfService) {
        this.hsfService = hsfService;
    }

    @Override
    public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) throws Exception {
        if (e.getState() == IdleState.WRITER_IDLE) {
            HsfChannel hsfChannel = hsfService.getChannels().get(e.getChannel().getId());
            if (hsfChannel != null) {
                //查询是否有消息需要推送 groupName为deviceid
                String groupName = hsfChannel.getChannelGroup().getName();
                //判断是否是rpc调用的groupName(D-Push的链接)
                Pattern pattern = Pattern.compile("(\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b)@(.+)");
                Matcher matcher = pattern.matcher(groupName);
                if (matcher.matches()) {
                    hsfChannel.write(Heartbeat.getSingleton());
                } else {
                    Set<String> set = OmitMessage.getPushInfo(groupName);
                    if (set.size() > 0) {
                        HashMap<String, String> pushMap = new HashMap<String, String>();
                        for (String mid : set) {
                            String mvalue = OmitMessage.getMessageInfo(mid);
                            pushMap.put(mid, mvalue);
                        }
                        hsfChannel.write(new PushRequest(pushMap));
                    }
                }

                logger.info("writer idle,sent a heartbeat.");
            } else {
                logger.warn("writer idle on channel({}), but hsfChannel is not managed.", e.getChannel());
            }
        } else if (e.getState() == IdleState.READER_IDLE) {
            logger.info("channel:{} is time out.", e.getChannel());
            handleUpstream(ctx, new DefaultExceptionEvent(e.getChannel(), new SocketTimeoutException(
                    "force to close channel(" + ctx.getChannel().getRemoteAddress() + "), reason: time out.")));

            e.getChannel().close();
            //
            HsfChannel hsfChannel = hsfService.getChannels().get(e.getChannel().getId());
            if (hsfChannel != null) {
                HsfChannelGroup channelGroup = hsfChannel.getChannelGroup();
                if (channelGroup != null) {
                    channelGroup.remove(hsfChannel);
                    //从缓存中删除
                    PhoneInfo.disConnInfo(channelGroup.getName(), String.valueOf(e.getChannel().getId()));
                    synchronized (channelGroup) {
                        channelGroup = hsfService.getGroups().get(channelGroup.getName());
                        if (channelGroup != null && channelGroup.isEmpty()) {
                            hsfService.getEventDispatcher().dispatchGroupRemovedEvent(ctx, hsfChannel, channelGroup.getName());
                        }
                    }
                }
            }
        }
        super.channelIdle(ctx, e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e.getMessage() == Heartbeat.getSingleton()) {
            //添加全局的心跳接收的统计代码
            HeartbeatStatisticInfo.incrementReceivedNum();
            return;
        }
        super.messageReceived(ctx, e);
    }
}
