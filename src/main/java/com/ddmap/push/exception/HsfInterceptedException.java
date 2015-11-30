package com.ddmap.push.exception;

/**
 * @Title: HsfInterceptedException.java
 * @Package com.ddmap.push.exception
 * @Description: Hsf调用被成功拦截
 * @author guo
 * @date 2012-5-17 下午14:02:47
 * @version V1.0
 */
public class HsfInterceptedException extends HsfRuntimeException{
	private static final long serialVersionUID = 5822623760553747362L;

	public HsfInterceptedException() {
		super();
	}

	public HsfInterceptedException(String message) {
		super(message);
	}

	public HsfInterceptedException(String message, Throwable cause) {
		super(message, cause);
	}

	public HsfInterceptedException(Throwable cause) {
		super(cause);
	}
}
