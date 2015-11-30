package com.ddmap.push.util;

/**
 * @Title: AsyncType.java
 * @Package com.ddmap.push.proxy
 * @Description: 异步方式
 * @author guo
 * @date 2011-9-30 下午3:12:10
 * @version V1.0
 */
public enum AsyncType {
	/**
	 * @Fields Default : 普通异步方式
	 */
	Default,
	/**
	 * @Fields Future : Future方式
	 */
	Future,
	/**
	 * @Fields Callback : Callback方式
	 */
	Callback,
	/**
	 * @Fields Sync : 同步方式
	 */
	Sync
}
