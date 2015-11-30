package com.ddmap.push.test.serviceargs;

import java.util.List;

/**
 * @Description: TODO
 * @author yangguo
 * @date 2012-5-8 下午6:00:26
 */
public class ArgServiceImpl implements ArgService {

	@Override
	public String testArgs(List<String> list, List<String> list2, List<String> list3, Object obj, boolean isBoy) {
		return "success";
	}

}
