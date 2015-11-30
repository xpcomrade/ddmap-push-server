package com.ddmap.push.netty.dispatcher.sync;

import com.ddmap.push.netty.dispatcher.InvokeResult;
import com.ddmap.push.netty.service.HsfService;

/**
 * @Title: SyncDispatchStrategy.java
 * @Package com.ddmap.push.netty.dispatcher.sync
 * @Description: 分发策略接口
 * @author guo
 * @date 2011-9-29 下午2:26:19
 * @version V1.0
 */
public interface SyncDispatchStrategy {

	HsfService getService();

	/**
	 * @Title: dispatch
	 * @Description: 分发消息
	 * @author guo
	 * @param message
	 *        消息
	 * @return DispatchResult 返回类型
	 */
	InvokeResult dispatch(Object message);
}
