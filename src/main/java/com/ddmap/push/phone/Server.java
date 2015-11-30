package com.ddmap.push.phone;

import com.ddmap.push.netty.channel.HsfChannel;
import com.ddmap.push.netty.channel.HsfChannelGroup;
import com.ddmap.push.netty.rservice.PushService;
import com.ddmap.push.netty.service.HsfAcceptor;
import com.ddmap.push.netty.service.HsfAcceptorImpl;
import com.ddmap.push.redis.PhoneInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Iterator;

/**
 * server启动类
 *
 *
 * @version V1.0
 * @Title: Server.java
 * @Package com.ddmap.hsf.phone
 * @date 2012-3-19 下午10:48:42
 */
public class Server {
    static private Logger logger = LoggerFactory.getLogger(Server.class);
    static public HsfAcceptor acceptor = new HsfAcceptorImpl();

    public static void main(String[] args) throws UnknownHostException {
        Runtime.getRuntime().addShutdownHook(new Thread(new Hook()));
        acceptor.registerService(new PushService());
        acceptor.bind(1883);
    }

    /**
     * 加一个关闭钩子，从而删除redis里面的链接信息
     */
    private static class Hook implements Runnable {
        @Override
        public void run() {
            Collection<HsfChannelGroup> collection = acceptor.getGroups().values();
            for (Iterator<HsfChannelGroup> iterator = collection.iterator(); iterator.hasNext(); ) {
                HsfChannelGroup hsfChannelGroup = iterator.next();
                for (HsfChannel hsfChannel : hsfChannelGroup.getChannels()) {
                    PhoneInfo.disConnInfo(hsfChannelGroup.getName(), String.valueOf(hsfChannel.getId()));
                    logger.info("Channel of groupName=({}) and channelId=({}) has been removed!", hsfChannelGroup.getName(), hsfChannel.getId());
                }
            }
        }
    }
}
