package com.ddmap.push.netty.handshake;

import java.util.Map;

import com.ddmap.push.pojo.HandshakeAck;

/**
 * @Title: ConnectorHandshakeProcessor.java
 * @Package com.ddmap.push.netty.handshake
 * @Description: 客户端握手处理接口
 * @author guo
 * @date 2011-11-22 上午3:41:28
 * @version V1.0
 */
public interface ConnectorHandshakeProcessor {
	/**
	 * @Title: getRequestAttachment
	 * @Description: 该对象将被放入握手请求消息包中
	 * @author guo
	 * @return Object    返回类型
	 */
	Object getRequestAttachment();

	/**
	 * @Title: getFinishAttachment
	 * @Description: 该对象将被放入握手完成消息包中
	 * @author guo
	 * @return Object    返回类型
	 */
	Object getFinishAttachment();

	/**
	 * @Title: getInitAttributes
	 * @Description: 该Map将在GroupCreated时，放入Group的attributes中
	 * @author guo
	 * @return Map<String,Object>    返回类型
	 */
	Map<String,Object> getInitAttributes();
	/**
	 * @Title: process
	 * @Description: 处理
	 * @author guo
	 * @param HandshakeAck
	 * @return void 返回类型
	 */
	void process(HandshakeAck handshakeAck);
}
