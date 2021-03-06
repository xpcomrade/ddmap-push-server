package com.ddmap.push.pojo;

import java.io.Serializable;

/**
 * @Title: Heartbeat.java
 * @Package com.ddmap.push.pojo
 * @Description: 心跳消息
 * @author guo
 * @date 2011-9-17 上午10:15:42
 * @version V1.0
 */
public final class Heartbeat implements Serializable {
	private static final long serialVersionUID = 1974602133338656765L;

	public static final byte[] BYTES = new byte[0];

	private static Heartbeat instance = new Heartbeat();

	public static Heartbeat getSingleton() {
		return instance;
	}

	private Heartbeat() {
	}
}
