package com.ddmap.push.test.cache;

/**
 * @Title: CacheService.java
 * @Package com.ddmap.push.test.cache
 * @Description: TODO(添加描述)
 * @author Administrator
 * @date 2012-3-19 下午11:21:15
 * @version V1.0
 */
public interface CacheService {
	public boolean set(String key, Object value);

	public Object get(String key);
}
