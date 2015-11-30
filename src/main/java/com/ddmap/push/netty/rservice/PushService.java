package com.ddmap.push.netty.rservice;

import com.ddmap.push.netty.channel.HsfChannel;
import com.ddmap.push.netty.channel.HsfChannelGroup;
import com.ddmap.push.phone.Server;
import com.ddmap.push.pojo.PushRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: guo
 * Date: 13-11-11
 * Time: 上午10:41
 */
public class PushService implements IPushService {
    static Map<String, HsfChannelGroup> map = Server.acceptor.getGroups();

    @Override
    public boolean push(String groupName, String messageId, String messageValue) {
        //获得groupName的链接
        HsfChannelGroup hsfChannelGroup = map.get(groupName);
        if (hsfChannelGroup != null) {
            HashMap<String, String> pushMap = new HashMap<String, String>();
            pushMap.put(messageId, messageValue);
            PushRequest pushRequest = new PushRequest(pushMap);
            List<HsfChannel> list = hsfChannelGroup.getChannels();
            for (HsfChannel hsfChannel : list) {
                hsfChannel.write(pushRequest);
            }
        }
        return true;
    }
}
