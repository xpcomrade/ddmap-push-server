package com.ddmap.push.test.cache;

import com.ddmap.push.netty.handler.upstream.LengthBasedDecoder;
import com.ddmap.push.netty.handler.downstream.LengthBasedEncoder;
import com.ddmap.push.netty.handler.downstream.CompressionDownstreamHandler;
import com.ddmap.push.netty.handler.downstream.SerializeDownstreamHandler;
import com.ddmap.push.netty.handler.upstream.DecompressionUpstreamHandler;
import com.ddmap.push.netty.handler.upstream.DeserializeUpstreamHandler;
import com.ddmap.push.netty.service.HsfAcceptor;
import com.ddmap.push.netty.service.HsfAcceptorImpl;

/**
 * @Title: Server.java
 * @Package com.ddmap.push.test.ehcache
 * @Description: TODO(添加描述)
 * @author Administrator
 * @date 2012-3-19 下午10:48:42
 * @version V1.0
 */
public class Server {
	public static void main(String[] args) {
		HsfAcceptor acceptor = new HsfAcceptorImpl();
		acceptor.getHandlers().clear();
		CacheSerializer serializer = new CacheSerializer();
		
		acceptor.getHandlers().put("encode", new LengthBasedEncoder());
		acceptor.getHandlers().put("compress", new CompressionDownstreamHandler());
		acceptor.getHandlers().put("serialize", new SerializeDownstreamHandler(serializer));

		acceptor.getHandlers().put("decode", new LengthBasedDecoder());
		acceptor.getHandlers().put("decompress", new DecompressionUpstreamHandler());
		acceptor.getHandlers().put("deserialize", new DeserializeUpstreamHandler(serializer));
		
		acceptor.registerService(new ByteCacheServiceImpl());
		acceptor.bind(8082);
	}
}
