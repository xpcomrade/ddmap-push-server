package com.ddmap.push.exception;

/**
 * @Title: HsfNoSuchServiceException.java
 * @Package com.ddmap.push.exception
 * @Description: Hsf未找到远程服务异常
 * @author guo
 * @date 2011-9-16 下午12:12:47
 * @version V1.0
 */
public class HsfNoSuchServiceException extends RuntimeException {
	private static final long serialVersionUID = 5822623760553747361L;

	public HsfNoSuchServiceException() {
		super();
	}

	public HsfNoSuchServiceException(String serviceName) {
		super("service \'" + serviceName + "\' is not registered");
	}
}
