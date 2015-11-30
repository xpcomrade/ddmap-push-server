package com.ddmap.push.statistic;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author guo
 * @version V1.0
 * @Title: HeartbeatStatisticInfo.java
 * @Package com.ddmap.push.statistic
 * @Description: 统计信息
 * @date 2012-2-27 上午08:41:12
 */
public class HeartbeatStatisticInfo implements Serializable {
    private static final long serialVersionUID = 7094331845793206401L;

    private static AtomicLong receivedNum = new AtomicLong();
    private static AtomicLong sentNum = new AtomicLong();

    public static AtomicLong getReceivedNum() {
        return receivedNum;
    }

    public static void incrementReceivedNum() {
        receivedNum.incrementAndGet();
    }

    public static AtomicLong getSentNum() {
        return sentNum;
    }

    public static void incrementSentNum() {
        sentNum.incrementAndGet();
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("HeartbeatStatisticInfo [");
        builder.append("receivedNum=");
        builder.append(receivedNum);
        builder.append(", sentNum=");
        builder.append(sentNum);
        builder.append("]");
        return builder.toString();
    }

}
