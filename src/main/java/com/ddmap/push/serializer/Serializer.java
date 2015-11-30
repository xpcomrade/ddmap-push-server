package com.ddmap.push.serializer;

/**
 * @ClassName: Serializer
 * @Description: 序列化类
 * @author guo
 * @date 2011-9-29 下午1:35:45
 * 
 */
public interface Serializer {
	/**
	 * @Title: init
	 * @Description: 初始化
	 * @author guo
	 * @param @throws Exception
	 * @return void 返回类型
	 */
	public void init() throws Exception;

	/**
	 * @Title: serialize
	 * @Description: 序列化
	 * @author guo
	 * @param object
	 * @param @throws Exception
	 * @return byte[] 返回类型
	 */
	public byte[] serialize(Object object) throws Exception;

	/**
	 * @Title: deserialize
	 * @Description: 反序列化
	 * @author guo
	 * @param bytes
	 * @param @throws Exception
	 * @return Object 返回类型
	 */
	public Object deserialize(byte[] bytes) throws Exception;

	/**
	 * @Title: register
	 * @Description: 注册类型
	 * @author guo
	 * @param class1
	 * @return void 返回类型
	 */
	public void register(Class<?> class1);
}
