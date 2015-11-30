package com.ddmap.push.test.reject;

import com.ddmap.push.netty.service.HsfAcceptor;
import com.ddmap.push.netty.service.HsfAcceptorImpl;
import com.ddmap.push.test.service.TestServiceImpl;
import com.ddmap.push.util.HsfOptions;


/**
 * @Title: Server.java
 * @Description: TODO(添加描述)
 * @date 2012-2-23 上午12:58:53
 * @version V1.0
 */
public class Server {
	public static void main(String[] args) {
		HsfAcceptor acceptor = new HsfAcceptorImpl();
		// 设置线程池队列大小为1
		acceptor.setOption(HsfOptions.EVENT_EXECUTOR_QUEUE_CAPACITY, 1);
		acceptor.registerService(new TestServiceImpl());
		
		acceptor.bind(8082);
	}
}
