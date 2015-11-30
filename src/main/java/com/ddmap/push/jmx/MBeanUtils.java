package com.ddmap.push.jmx;

import javax.management.ObjectName;

import com.ddmap.push.jmx.management.helper.MBeanRegistration;

/**
 * 
 */
public class MBeanUtils {

	public static void registerMBean(Object mbean, String packageName, String name) throws Exception{
		new MBeanRegistration(mbean, new ObjectName(packageName+":name="+name)).register();
	}
	
	public static void register(Object mbean){
		try {
			new MBeanRegistration(mbean, new ObjectName(mbean.getClass().getPackage().getName() + ":name="
					+ mbean.getClass().getSimpleName() + "-" + mbean.hashCode())).register();
			
		}catch(Exception e){}
	}
}
