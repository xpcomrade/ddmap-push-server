package com.ddmap.push.pojo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

/**
 * User: guo
 * Date: 13-9-27
 * Time: 下午3:46
 * server推送给手机端的消息pojo
 */
public class PushRequest implements Serializable {
    private static final long serialVersionUID = 7219716206219584167L;
    private HashMap<String, String> map;

    public HashMap getMap() {
        return map;
    }

    public void setMap(HashMap map) {
        this.map = map;
    }

    public PushRequest() {
    }

    /**
     * @param map  推送消息的map结构，messageId为Key，messageValue为Value
     */
    public PushRequest(HashMap<String, String> map) {
        this.map = map;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PushRequest [");
        int i = 0;
        for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext(); ) {
            String messageId = iterator.next();
            if (i++ > 0) {
                builder.append(",");
            }
            builder.append(messageId);
            builder.append("=");
            builder.append(map.get(messageId));
        }
        builder.append("]");
        return builder.toString();
    }
}
