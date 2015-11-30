package com.ddmap.push.test.serviceargs;

import java.util.List;

import com.ddmap.push.annotation.RemoteServiceContract;

/**
 * @Description: TODO
 * @author yangguo
 * @date 2012-5-8 下午5:59:25
 */
@RemoteServiceContract
public interface ArgService {
	public String testArgs(List<String> list,List<String> list2,List<String> list3,Object obj, boolean isBoy);
}
