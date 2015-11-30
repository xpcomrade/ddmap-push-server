package com.ddmap.push.netty.rservice;

import com.ddmap.push.annotation.RemoteServiceContract;

/**
 * Created with IntelliJ IDEA.
 * User: guo
 * Date: 13-11-11
 * Time: 上午10:38
 * push推送远程调用
 */
@RemoteServiceContract
public interface IPushService {
    public boolean push(String groupName,String messageId,String messageValue);
}
