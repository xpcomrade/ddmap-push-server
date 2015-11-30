package com.ddmap.push.pojo;

import java.io.Serializable;
import java.util.Set;

/**
 * User: guo
 * Date: 13-11-26
 * Time: 下午4:05
 * To change this template use File | Settings | File Templates.
 */
public class OpenRequest implements Serializable {
    private static final long serialVersionUID = -4207832119843232124L;
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

    public OpenRequest() {
    }

    public OpenRequest(String groupName, Set<String> messageIds) {
        this.groupName = groupName;
        this.messageIds = messageIds;
    }
}
