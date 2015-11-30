package com.ddmap.push.util;

import java.util.Random;

/**
 * 
 * @author guo
 */
public class RandomUtil {
	private static Random random = new Random();

	public static int nextInt() {
		return random.nextInt();
	}

	public static int nextInt(int n) {
		return random.nextInt(n);
	}
}
