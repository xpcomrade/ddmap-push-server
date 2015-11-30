package com.ddmap.push.pojo;

import java.io.Serializable;
import java.util.Set;

/**
 * User: guo
 * Date: 13-9-30
 * Time: 上午10:33
 * <p/>
 * 手机端回应消息到达的pojo
 */
public class PushAck implements Serializable {
    private static final long serialVersionUID = -4106238146670549805L;
    private String groupName;
    private Set<String> messageIds;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Set<String> getMessageIds() {
        return messageIds;
    }

    public void setMessageIds(Set<String> messageIds) {
        this.messageIds = messageIds;
    }

    public PushAck() {
    }

    public PushAck(String groupName, Set<String> messageIds) {
        this.groupName = groupName;
        this.messageIds = messageIds;
    }
}
