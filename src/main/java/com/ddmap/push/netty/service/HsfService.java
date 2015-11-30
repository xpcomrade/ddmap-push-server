package com.ddmap.push.netty.service;

import java.util.EventListener;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import org.jboss.netty.channel.ChannelHandler;
import com.ddmap.push.netty.channel.FlowManager;
import com.ddmap.push.netty.channel.HsfChannel;
import com.ddmap.push.netty.channel.HsfChannelGroup;
import com.ddmap.push.netty.event.EventDispatcher;
import com.ddmap.push.netty.interceptor.PreDispatchInterceptor;
import com.ddmap.push.pojo.ServiceEntry;
import com.ddmap.push.util.ConcurrentArrayListHashMap;

/**
 * @Title: HsfService.java
 * @Package com.ddmap.push.netty.service
 * @Description: Hsf服务接口
 * @author guo
 * @date 2011-9-23 下午12:17:54
 * @version V1.0
 */
@SuppressWarnings("rawtypes")
public interface HsfService {
	/**
	 * @Title: isAlived
	 * @Description: 是否处于活动状态
	 * @author guo
	 * @return boolean 返回类型
	 */
	boolean isAlived();

	/**
	 * @Title: getOptions
	 * @Description: 获取Options选项设置
	 * @author guo
	 * @return Map<String,Object> 返回类型
	 */
	Map<String, Object> getOptions();

	Object getOption(String opName);

	/**
	 * @Title: setOptions
	 * @Description: 设置Options选项
	 * @author guo
	 * @param options
	 *        选项参数
	 * @return void 返回类型
	 */
	void setOptions(Map<String, Object> options);

	void setOption(String opName, Object opValue);

	/**
	 * @Title: getHandlers
	 * @Description: 获取自定义Handler
	 * @author guo
	 * @return LinkedHashMap<String,ChannelHandler> 返回类型
	 */
	LinkedHashMap<String, ChannelHandler> getHandlers();

	/**
	 * @Title: setHandlers
	 * @Description: 设置自定义Handler
	 * @author guo
	 * @param handlers
	 *        Handler集合
	 * @return void 返回类型
	 */
	void setHandlers(LinkedHashMap<String, ChannelHandler> handlers);

	/**
	 * @Title: getListeners
	 * @Description: 获取监听器集合</br>{@link com.ddmap.push.netty.listener.ChannelEventListener},
	 *               {@link com.ddmap.push.netty.listener.MessageEventListener},
	 *               {@link com.ddmap.push.netty.listener.ExceptionEventListener}
	 * @author guo
	 * @return List<EventListener> 返回类型
	 */
	List<EventListener> getListeners();

	/**
	 * @Title: getListeners
	 * @Description: 设置监听器集合，
	 * @author guo
	 * @param listeners
	 *        监听器集合</br>{@link com.ddmap.push.netty.listener.ChannelEventListener},
	 *        {@link com.ddmap.push.netty.listener.MessageEventListener},
	 *        {@link com.ddmap.push.netty.listener.ExceptionEventListener}
	 * @return List<EventListener> 返回类型
	 */
	void setListeners(List<EventListener> listeners);

	/**
	 * @Title: getGroups
	 * @Description: 获取已经建立连接的Channel组集合
	 * @author guo
	 * @return Map<String, HsfChannelGroup> 返回类型
	 */
	ConcurrentArrayListHashMap<String, HsfChannelGroup> getGroups();

	/**
	 * @Title: getChannels
	 * @Description:
	 * @author guo
	 * @param id
	 * @return Map<Integer, HsfChannel> 返回类型
	 */
	Map<Integer, HsfChannel> getChannels();

	/**
	 * @Title: shutdown
	 * @Description: 关闭服务
	 * @author guo
	 * @return void 返回类型
	 */
	void shutdown();

	/**
	 * @Title: getGroupName
	 * @Description: 获取组名称
	 * @author guo
	 * @return String 返回类型
	 */
	String getGroupName();

	/**
	 * @Title: setGroupName
	 * @Description: 设置组名称
	 * @author guo
	 * @param groupName
	 *        组名称
	 * @return void 返回类型
	 */
	void setGroupName(String groupName);

	/**
	 * @Title: getServices
	 * @Description: 获取注册的服务集合
	 * @author guo
	 * @return LinkedHashMap<String,ServiceEntry> 返回类型
	 */
	LinkedHashMap<String, ServiceEntry> getServices();

	/**
	 * @Title: setServices
	 * @Description: 注册服务
	 * @author guo
	 * @param services
	 *        服务集合
	 * @return void 返回类型
	 */
	void setServices(List<Object> services);

	/**
	 * @Title: registerService
	 * @Description: 注册服务
	 * @author guo
	 * @param serviceInterface
	 *        服务接口
	 * @param service
	 *        服务对象
	 * @return void 返回类型
	 */
	void registerService(Class<?> serviceInterface, Object service);

	/**
	 * @Title: registerService
	 * @Description: 注册服务
	 * @author guo
	 * @param name
	 *        服务名称
	 * @param service
	 *        服务对象
	 * @return void 返回类型
	 */
	void registerService(String name, Object service);

	/**
	 * @Title: registerService
	 * @Description: 注册拥有RemoteServiceContract注解的服务
	 * @author guo
	 * @param service
	 * @return void 返回类型
	 */
	void registerService(Object service);

	FlowManager getFlowManager();

	void setFlowManager(FlowManager flowManager);

	EventDispatcher getEventDispatcher();

	void setEventExecutor(Executor executor);

	LinkedList<PreDispatchInterceptor> getPreDispatchInterceptors();

	void setPreDispatchInterceptors(LinkedList<PreDispatchInterceptor> preDispatchInterceptors);
}
