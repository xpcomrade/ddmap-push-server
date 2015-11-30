package com.ddmap.push.pojo;

import java.io.Serializable;
import java.util.Set;

/**
 * User: guo
 * Date: 13-11-26
 * Time: 下午4:06
 * To change this template use File | Settings | File Templates.
 */
public class OpenAck implements Serializable {
    private static final long serialVersionUID = -8941722414552671780L;
    private Set<String> messageIds;

    public Set<String> getMessageIds() {
        return messageIds;
    }

    public void setMessageIds(Set<String> messageIds) {
        this.messageIds = messageIds;
    }

    public OpenAck() {
    }

    public OpenAck(Set<String> set) {
        this.messageIds = set;
    }
}
