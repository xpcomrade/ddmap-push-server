package com.ddmap.push.configserver.pojo;

/**
 * @Title: AddressType.java
 * @Package com.ddmap.push.configserver.pojo
 * @Description: 地址类型
 * @author guo
 * @date 2012-3-22 下午12:03:15
 * @version V1.0
 */
public enum AddressType {
	/**
	 * @Fields Address : 全地址方式
	 */
	Address,
	/**
	 * @Fields Port : 端口方式，使用此方式，Ip自动采集
	 */
	Port
}
