package com.ddmap.push.exception;

/**
 * @Title: HsfException.java
 * @Package com.ddmap.push.exception
 * @Description: Hsf运行时异常
 * @author guo
 * @date 2011-9-16 下午12:12:47
 * @version V1.0
 */
public class HsfRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 5822623760553747360L;

	public HsfRuntimeException() {
		super();
	}

	public HsfRuntimeException(String message) {
		super(message);
	}

	public HsfRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public HsfRuntimeException(Throwable cause) {
		super(cause);
	}
}
