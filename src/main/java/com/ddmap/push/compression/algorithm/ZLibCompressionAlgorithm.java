package com.ddmap.push.compression.algorithm;

import java.io.IOException;

import com.ddmap.push.util.ZLibUtils;

/**
 * @Title: ZLibCompressionAlgorithm.java
 * @Package com.ddmap.push.compression
 * @Description: ZLib压缩算法实现
 * @author guo
 * @date 2011-9-16 下午4:06:02
 * @version V1.0
 */
public class ZLibCompressionAlgorithm implements CompressionAlgorithm {

	public byte[] compress(byte[] buffer) throws IOException {
		return ZLibUtils.compress(buffer);
	}

	public byte[] decompress(byte[] buffer) throws IOException {
		return ZLibUtils.decompress(buffer);
	}
}
