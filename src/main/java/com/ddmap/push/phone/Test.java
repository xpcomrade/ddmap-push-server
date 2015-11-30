package com.ddmap.push.phone;

import com.ddmap.push.netty.rservice.IPushService;
import com.ddmap.push.netty.service.HsfConnector;
import com.ddmap.push.netty.service.HsfConnectorImpl;
import com.ddmap.push.proxy.ServiceProxyFactory;
import com.ddmap.push.util.AddressUtil;

/**
 * Created by wangzp
 * Date: 2015/11/20 11:40
 * Copyright (c) 2015, xpcomrade@gmail.com All Rights Reserved.
 * Description: TODO(这里用一句话描述这个类的作用). <br/>
 */
public class Test {
    public static void main(String[] args) {
        HsfConnector connector = new HsfConnectorImpl();
        try {
            connector.connect(AddressUtil.parseAddress("192.168.9.217" + ":" + "1883"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        IPushService service = ServiceProxyFactory.getRoundFactoryInstance(connector).wrapSyncProxy(IPushService.class);
        service.push("865027020096902", "xpcomrade", "hi, xpcomrade");

    }
}
