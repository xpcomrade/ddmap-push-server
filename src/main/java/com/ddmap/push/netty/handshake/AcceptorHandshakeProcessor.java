package com.ddmap.push.netty.handshake;

import com.ddmap.push.pojo.HandshakeFinish;
import com.ddmap.push.pojo.HandshakeRequest;

import java.util.Map;

/**
 * @author guo
 * @version V1.0
 * @Title: HandshakeProcessor.java
 * @Package com.ddmap.push.netty.handshake
 * @Description: 服务端握手处理
 * @date 2011-11-24 下午2:18:15
 */
public interface AcceptorHandshakeProcessor {
    /**
     * @return Object 返回类型
     * @Title: getAckAttachment
     * @Description: 该对象将被放入握手Ack消息包中
     * @author guo
     */
    Object getAckAttachment();

    /**
     * @return Map<String,Object> 返回类型
     * @Title: getInitAttributes
     * @Description: 该Map将在GroupCreated时，放入Group的attribute中
     * @author guo
     */
    Map<String, Object> getInitAttributes();

    void process(HandshakeRequest handshakeRequest);

    void process(HandshakeFinish handshakeFinish);
}
