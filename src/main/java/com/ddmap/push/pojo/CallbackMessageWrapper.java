package com.ddmap.push.pojo;
/**
 * CallbackMessage包装类，以实现对无法派生自CallbackMessage接口的对象的包括
 *
 * @Title: CallbackMessageWrapper.java
 * @Package com.ddmap.push.pojo
 * @author guo
 * @date 2012-3-7 下午2:56:55
 * @version V1.0
 */
public final class CallbackMessageWrapper<T> implements CallbackMessage<T> {
	private T msg;
	
	public CallbackMessageWrapper(T msg){
		this.msg = msg;
	}
	
	@Override
	public T getMessage() {
		return msg;
	}

}
