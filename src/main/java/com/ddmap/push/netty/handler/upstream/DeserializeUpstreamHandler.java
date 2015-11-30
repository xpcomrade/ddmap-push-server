package com.ddmap.push.netty.handler.upstream;

import com.ddmap.push.pojo.Heartbeat;
import com.ddmap.push.serializer.KryoSerializer;
import com.ddmap.push.serializer.Serializer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.UpstreamMessageEvent;

/**
 * @author guo
 * @version V1.0
 * @Title: DeserializeUpstreamHandler.java
 * @Package com.ddmap.push.netty.channelhandler.downstream
 * @Description: 反序列化
 * @date 2011-9-16 下午4:45:59
 */
public class DeserializeUpstreamHandler extends SimpleChannelUpstreamHandler {
    private Serializer serializer = new KryoSerializer();

    public DeserializeUpstreamHandler() {
    }

    public DeserializeUpstreamHandler(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e.getMessage() == null) {
            return;
        } else if (e.getMessage() instanceof byte[]) {
            byte[] bytes = (byte[]) e.getMessage();
            Object msg;
            if (bytes.length == 0) {
                msg = Heartbeat.getSingleton();
            } else {
                try {
                    msg = serializer.deserialize(bytes);
                } catch (Exception ex) {
                    throw ex;
                }
            }
            UpstreamMessageEvent event = new UpstreamMessageEvent(e.getChannel(), msg, e.getRemoteAddress());
            super.messageReceived(ctx, event);
        } else {
            super.messageReceived(ctx, e);
        }
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }
}