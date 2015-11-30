package com.ddmap.push.exception;

/**
 * @Title: HsfInvocationException.java
 * @Package com.ddmap.push.exception
 * @Description: Hsf远程服务异常
 * @author guo
 * @date 2011-9-16 下午12:12:47
 * @version V1.0
 */
public class HsfOperationException extends HsfRuntimeException{
	private static final long serialVersionUID = 5822623760553747361L;

	public HsfOperationException() {
		super();
	}

	public HsfOperationException(String message) {
		super(message);
	}

	public HsfOperationException(String message, Throwable cause) {
		super(message, cause);
	}

	public HsfOperationException(Throwable cause) {
		super(cause);
	}
}
