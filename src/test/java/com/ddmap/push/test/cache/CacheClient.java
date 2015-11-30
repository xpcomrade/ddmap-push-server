package com.ddmap.push.test.cache;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.ddmap.push.netty.handler.upstream.LengthBasedDecoder;
import com.ddmap.push.netty.handler.downstream.LengthBasedEncoder;
import com.ddmap.push.netty.handler.downstream.CompressionDownstreamHandler;
import com.ddmap.push.netty.handler.downstream.SerializeDownstreamHandler;
import com.ddmap.push.netty.handler.upstream.DecompressionUpstreamHandler;
import com.ddmap.push.netty.handler.upstream.DeserializeUpstreamHandler;
import com.ddmap.push.netty.service.HsfConnector;
import com.ddmap.push.netty.service.HsfConnectorImpl;
import com.ddmap.push.proxy.ServiceProxyFactory;
import com.ddmap.push.serializer.KryoSerializer;
import com.ddmap.push.serializer.Serializer;
import com.ddmap.push.util.AddressUtil;
import com.ddmap.push.util.ReflectionUtil;

/**
 * @Title: EhcacheClient.java
 * @Package com.ddmap.push.test.ehcache
 * @Description: TODO(添加描述)
 * @author Administrator
 * @date 2012-3-19 下午11:05:00
 * @version V1.0
 */
public class CacheClient {
	private CacheService cacheService;

	public CacheClient(String address) {
		HsfConnector connector = new HsfConnectorImpl();
		connector.getHandlers().clear();
		CacheSerializer serializer = new CacheSerializer();

		connector.getHandlers().put("encode", new LengthBasedEncoder());
		connector.getHandlers().put("compress", new CompressionDownstreamHandler());
		connector.getHandlers().put("serialize", new SerializeDownstreamHandler(serializer));

		connector.getHandlers().put("decode", new LengthBasedDecoder());
		connector.getHandlers().put("decompress", new DecompressionUpstreamHandler());
		connector.getHandlers().put("deserialize", new DeserializeUpstreamHandler(serializer));

		connector.connect(AddressUtil.parseAddress(address));

		InvocationHandler requestHandler = new EhcacheProxyHandler(connector);

		// 创建代理
		cacheService = (CacheService) Proxy.newProxyInstance(getClassLoader(CacheService.class),
				new Class[] { CacheService.class }, requestHandler);

	}

	public boolean set(String key, Object value) {
		return cacheService.set(key, value);
	}

	public Object get(String key) {
		return cacheService.get(key);
	}

	private static ClassLoader getClassLoader(Class<?> clazz) {
		if (clazz != null && clazz.getClassLoader() != null) {
			return clazz.getClassLoader();
		}

		if (Thread.currentThread().getContextClassLoader() != null) {
			return Thread.currentThread().getContextClassLoader();
		}

		return ClassLoader.getSystemClassLoader();
	}

	private static class EhcacheProxyHandler implements InvocationHandler {

		private ByteCacheService ehcacheService;
		private Serializer serializer = new KryoSerializer();

		public EhcacheProxyHandler(HsfConnector hsfConnector) {
			ehcacheService = ServiceProxyFactory.getRoundFactoryInstance(hsfConnector).wrapSyncProxy(
					ByteCacheService.class);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			String methodName = method.getName();
			if ("set".equals(methodName) && args[1] != null) {
				args[1] = serializer.serialize(args[1]);
			}
			//
			Object retValue = ReflectionUtil.invoke(ehcacheService, methodName, args);
			if ("get".equals(methodName) && retValue != null) {
				retValue = serializer.deserialize((byte[]) retValue);
			}

			return retValue;
		}
	}
}
