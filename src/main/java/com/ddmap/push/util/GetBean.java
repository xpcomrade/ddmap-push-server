package com.ddmap.push.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * User: 杨果
 * Date: 12-3-30
 * Time: 下午2:43
 * To change this template use File | Settings | File Templates.
 */
public class GetBean {
    static ApplicationContext context = new ClassPathXmlApplicationContext("spring/spring-*.xml");

    public static <T> T get(String beanName) {
        return (T) context.getBean(beanName);
    }
}
