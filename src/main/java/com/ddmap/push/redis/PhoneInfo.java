package com.ddmap.push.redis;

import com.ddmap.push.util.GetBean;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * User: guo
 * Date: 13-9-24
 * Time: 上午10:20
 * 手机端的连接状态需要存储在redis中，后面的消息推送将利用该信息进行路由
 * 采用hashmap存储手机端链接信息，每个group就是一个hashmap，key为channelId，value为serverId
 */
public class PhoneInfo {
    static RedisTemplate redisTemplate0 = GetBean.get("redisTemplate0");

    /**
     * 将手机连接信息写入redis，连接信息有效时间为7天
     * 使用的数据结构是redis的hashs
     *
     * @param groupName 手机唯一id
     * @param channelId channel链接的唯一id
     * @param ip        服务器的ip
     */
    public static void setConnInfo(final String groupName, final String channelId, final String ip) {
        redisTemplate0.execute(new SessionCallback<List<Object>>() {
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                operations.opsForHash().put(groupName, channelId, ip);
                operations.expire(groupName, 7, TimeUnit.DAYS);
                return operations.exec();
            }
        });
    }

    /**
     * 手机断开时，将链接信息从redis中删除
     *
     * @param groupName
     * @param channelId
     */
    public static void disConnInfo(String groupName, String channelId) {
        BoundHashOperations boundHashOperations = redisTemplate0.boundHashOps(groupName);
        boundHashOperations.delete(channelId);
    }
}
