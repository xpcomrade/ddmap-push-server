package com.ddmap.push.netty.dispatcher.async;

import com.ddmap.push.netty.dispatcher.InvokeResult;
import com.ddmap.push.netty.service.HsfService;
import com.ddmap.push.util.AsyncCallback;
import com.ddmap.push.util.AsyncType;

/**
 * @Title: AsyncDispatchStrategy.java
 * @Package com.ddmap.push.netty.dispatcher.async
 * @Description: 异步分发策略接口
 * @author guo
 * @date 2011-9-29 下午2:14:14
 * @version V1.0
 */
public interface AsyncDispatchStrategy {

	/**
	 * @Title: getService
	 * @Description: 获取Hsf服务
	 * @author guo
	 * @return HsfService 返回类型
	 */
	HsfService getService();

	/**
	 * @Title: dispatch
	 * @Description: 分发消息
	 * @author guo
	 * @param message
	 *        消息
	 * @param asyncType
	 *        异步方式
	 * @return DispatchResult<Channel, Object> 返回类型
	 */
	InvokeResult dispatch(Object message, AsyncType asyncType);

	/**
	 * @Title: dispatch
	 * @Description: 分发消息
	 * @author guo
	 * @param message
	 *        消息
	 * @param callback
	 *        回调
	 * @return DispatchResult<Channel, Object> 返回类型
	 */
	InvokeResult dispatch(Object message, AsyncCallback<?> callback);
}
