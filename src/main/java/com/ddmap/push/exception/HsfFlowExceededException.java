package com.ddmap.push.exception;

/**
 * 流量超限异常类
 * @author guo
 * 
 */
public class HsfFlowExceededException extends HsfRuntimeException {
	private static final long serialVersionUID = 3028665302400309063L;

	public HsfFlowExceededException() {
		super();
	}

	public HsfFlowExceededException(String message) {
		super(message);
	}

	public HsfFlowExceededException(String message, Throwable cause) {
		super(message, cause);
	}

	public HsfFlowExceededException(Throwable cause) {
		super(cause);
	}
}
