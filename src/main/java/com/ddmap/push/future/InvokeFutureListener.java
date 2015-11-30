package com.ddmap.push.future;

/**
 * @Title: InvokeFutureListener.java
 * @Package com.ddmap.push.future
 * @Description: InvokeFuture监听器
 * @author guo
 * @date 2011-11-17 下午3:02:29
 * @version V1.0
 */
public interface InvokeFutureListener<T> {
	void operationComplete(InvokeFuture<T> future) throws Exception;
}
