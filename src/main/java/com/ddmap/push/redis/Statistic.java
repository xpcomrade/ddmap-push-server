package com.ddmap.push.redis;

import com.ddmap.push.util.GetBean;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * User: guo
 * Date: 13-9-24
 * Time: 下午1:32
 * 对推送的结果进行统计，统计数据默认保存30天
 */
public class Statistic {
    static RedisTemplate redisTemplate2 = GetBean.get("redisTemplate2");

    /**
     * 指定messageid数据送达到手机的数量
     *
     * @param messageId 推送消息的id
     */
    public static void setCountReach(final String messageId) {
        redisTemplate2.execute(new SessionCallback<List<Object>>() {
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                operations.opsForValue().increment("r-" + messageId, 1L);
                operations.expire("r-" + messageId, 7, TimeUnit.DAYS);
                return operations.exec();
            }
        });
    }

    /**
     * 指定messageid数据送达到手机后，用户点击的打开数
     *
     * @param messageId 推送的消息id
     */
    public static void setCountOpen(final String messageId) {
        redisTemplate2.execute(new SessionCallback<List<Object>>() {
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                operations.opsForValue().increment("o-" + messageId, 1L);
                operations.expire("o-" + messageId, 7, TimeUnit.DAYS);
                return operations.exec();
            }
        });
    }
}
