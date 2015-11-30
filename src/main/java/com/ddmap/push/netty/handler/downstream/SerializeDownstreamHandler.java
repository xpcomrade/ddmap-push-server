package com.ddmap.push.netty.handler.downstream;

import com.ddmap.push.pojo.*;
import com.ddmap.push.serializer.KryoSerializer;
import com.ddmap.push.serializer.Serializer;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import static org.jboss.netty.channel.Channels.write;

/**
 * @author guo
 * @version V1.0
 * @Title: SerializeDownstreamHandler.java
 * @Package com.ddmap.push.netty.channelhandler.downstream
 * @Description: 序列化
 * @date 2011-9-16 下午4:45:59
 */
public class SerializeDownstreamHandler implements ChannelDownstreamHandler {
    private  Serializer serializer = new KryoSerializer();

    public SerializeDownstreamHandler() {
    }

    public SerializeDownstreamHandler(Serializer serializer) {
        this.serializer = serializer;
    }

    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (!(e instanceof MessageEvent)) {
            ctx.sendDownstream(e);
            return;
        }

        MessageEvent event = (MessageEvent) e;
        Object originalMessage = event.getMessage();
        Object encodedMessage = originalMessage;

        if (!(originalMessage instanceof Heartbeat)) {
            encodedMessage = serializer.serialize(originalMessage);
        } else {
            encodedMessage = Heartbeat.BYTES;
        }

        if (originalMessage == encodedMessage) {
            ctx.sendDownstream(e);
        } else if (encodedMessage != null) {
            write(ctx, e.getFuture(), encodedMessage, event.getRemoteAddress());
        }
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }
}
