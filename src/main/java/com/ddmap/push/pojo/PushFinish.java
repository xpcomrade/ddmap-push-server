package com.ddmap.push.pojo;

import java.io.Serializable;
import java.util.Set;

/**
 * User: guo
 * Date: 13-11-4
 * Time: 下午5:37
 * server端回应手机端ack已收到的pojo
 */
public class PushFinish implements Serializable {
    private static final long serialVersionUID = 6305489909030247657L;
    private Set<String> messageIds;

    public Set<String> getMessageIds() {
        return messageIds;
    }

    public void setMessageIds(Set<String> messageIds) {
        this.messageIds = messageIds;
    }

    public PushFinish(Set<String> messageIds) {
        this.messageIds = messageIds;
    }

    public PushFinish() {
    }
}
