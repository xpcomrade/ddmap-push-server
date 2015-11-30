package com.ddmap.push.netty.listener.impl;

import com.ddmap.push.netty.channel.HsfChannel;
import com.ddmap.push.netty.listener.EventBehavior;
import com.ddmap.push.netty.listener.MessageEventListener;
import com.ddmap.push.pojo.OpenRequest;
import com.ddmap.push.pojo.PushAck;
import com.ddmap.push.pojo.PushFinish;
import com.ddmap.push.redis.OmitMessage;
import com.ddmap.push.redis.Statistic;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: guo
 * Date: 13-11-6
 * Time: 下午4:57
 * 处理跟手机端交互（确认推送已收到和已打开）
 */
public class PushMessageEventListener implements MessageEventListener {
    @Override
    public EventBehavior messageReceived(ChannelHandlerContext ctx, HsfChannel channel, MessageEvent e) {
        if (e.getMessage() != null) {
            Set<String> set = new HashSet<String>();
            //处理推送确认请求
            if (e.getMessage() instanceof PushAck) {
                PushAck pushAck = (PushAck) e.getMessage();
                for (Iterator<String> iterator = pushAck.getMessageIds().iterator(); iterator.hasNext(); ) {
                    String messageId = iterator.next();
                    //删除缓存中已经收到的消息
                    OmitMessage.delPushInfo(pushAck.getGroupName(), messageId);
                    //对该消息的接收数进行计数
                    Statistic.setCountReach(messageId);
                    set.add(messageId);
                }

                //回送pushFinish
                PushFinish pushFinish = new PushFinish(set);
                channel.write(pushFinish);
            }
            if(e.getMessage() instanceof OpenRequest){
                OpenRequest openRequest=(OpenRequest)e.getMessage();
                for(Iterator<String> iterator=openRequest.getMessageIds().iterator();iterator.hasNext();){
                    String messageId=iterator.next();
                    Statistic.setCountOpen(messageId);
                }
            }
        }
        return EventBehavior.Continue;
    }
}
