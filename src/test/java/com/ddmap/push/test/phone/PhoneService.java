package com.ddmap.push.test.phone;

import com.ddmap.push.annotation.RemoteServiceContract;

/**
 * @Title: PhoneService.java
 * @Package com.ddmap.push.test.phone
 * @date 2012-3-20 上午12:07:10
 * @version V1.0
 */
@RemoteServiceContract
public interface PhoneService {
	public byte[] doExecute(byte[] msg);
}
