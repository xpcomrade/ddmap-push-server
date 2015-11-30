package com.ddmap.push.test.service;

import com.ddmap.push.annotation.RemoteServiceContract;

/**
 * @Title: TestService.java
 * @Description: TODO(添加描述)
 * @date 2012-2-23 上午12:59:49
 * @version V1.0
 */
@RemoteServiceContract
public interface TestService {
	String test(String ctx);
}
