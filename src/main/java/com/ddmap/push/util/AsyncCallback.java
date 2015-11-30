package com.ddmap.push.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ddmap.push.netty.channel.HsfChannel;

/**
 * @Title: AsyncCallback.java
 * @Package com.ddmap.push.proxy
 * @Description: 异步处理回调
 * @author guo
 * @date 2011-9-17 下午2:56:13
 * @version V1.0
 */
public abstract class AsyncCallback<TMessage> {
	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * @Title: doCallback
	 * @Description: 回调处理消息
	 * @author guo
	 * @param data
	 *        消息
	 * @return void 返回类型
	 */
	public abstract void doCallback(TMessage data);

	/**
	 * @Title: doExceptionCaught
	 * @Description: 处理异常
	 * @author guo
	 * @param ex
	 * @param channel
	 * @return void 返回类型
	 */
	public void doExceptionCaught(Throwable ex, HsfChannel channel, Object param) {
		logger.error("send msg:{} to channel({}) occurs exception:{}", new Object[] { param,
				channel.getRemoteAddress(), StackTraceUtil.getStackTrace(ex) });
	}
}