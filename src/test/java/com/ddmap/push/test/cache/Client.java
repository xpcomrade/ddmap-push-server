package com.ddmap.push.test.cache;

import java.util.concurrent.ExecutionException;

public class Client {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		CacheClient cacheClient = new CacheClient("127.0.0.1:8082");
		System.out.println(cacheClient.get("name"));
		cacheClient.set("name", "yangguo");
		System.out.println(cacheClient.get("name"));

		// ExecutorService executorService = Executors.newFixedThreadPool(150);
		// long begin = System.currentTimeMillis();
		//
		// for (int i = 0; i < 1000000; i++) {
		// final int j = i;
		// executorService.execute(new Runnable() {
		//
		// @Override
		// public void run() {
		// try {
		// testService.test("大家都有过复制一个大文件时，久久等待却不见结束，明明很着急却不能取消的情况吧——一旦取消，一切都要从头开始！在Windows 8");
		// } catch (Exception e) {
		// // System.err.println("error while i = " + j + " " + e.getMessage());
		// }
		// }
		// });
		// }
		// executorService.shutdown();
		// executorService.awaitTermination(10, TimeUnit.DAYS);
		//
		// long end = System.currentTimeMillis();
		// System.out.println("cost:" + (end - begin) + "ms");
	}
}
