package com.ddmap.push.redis;

import com.ddmap.push.util.GetBean;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;

/**
 * User: guo
 * Date: 13-9-24
 * Time: 上午10:56
 */
public class OmitMessage {
    static RedisTemplate redisTemplate1 = GetBean.get("redisTemplate1");

    /**
     * 根据推送的消息id获取消息内容
     *
     * @param messageId 推送消息的唯一id
     * @return
     */
    public static String getMessageInfo(final String messageId) {
        BoundValueOperations<String, String> boundValueOperations = redisTemplate1.boundValueOps("MI_" + messageId);
        return boundValueOperations.get();
    }

    /**
     * 根据手机的唯一标识，获得用户需要推送的消息id
     *
     * @param groupName 手机唯一标识
     * @return
     */
    public static Set<String> getPushInfo(String groupName) {
        BoundSetOperations<String, String> boundSetOperations = redisTemplate1.boundSetOps(groupName);
        return boundSetOperations.members();
    }

    /**
     * 用户相应某消息已经收到，则删除该消息的id
     *
     * @param groupName 手机唯一的串号
     * @param messageId 消息的唯一id
     */
    public static void delPushInfo(String groupName, String messageId) {
        BoundSetOperations<String, String> boundSetOperations = redisTemplate1.boundSetOps(groupName);
        boundSetOperations.remove(messageId);
    }
}
