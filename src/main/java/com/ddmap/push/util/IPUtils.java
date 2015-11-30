package com.ddmap.push.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * User: guo
 * Date: 13-11-5
 * Time: 下午4:33
 * 获得指定网卡的ip
 */
public class IPUtils {
    /**
     * 获得内部网络IPV4地址，回环地址除外
     * @return
     */
    public static String getInnerIp() {
        String ip = null;
        Enumeration<NetworkInterface> enumeration = null;
        try {
            enumeration = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        for (; enumeration.hasMoreElements(); ) {
            NetworkInterface networkInterface = enumeration.nextElement();
            if (networkInterface.getName().startsWith("e")) {
                Enumeration<InetAddress> ias = networkInterface.getInetAddresses();
                for (; ias.hasMoreElements(); ) {
                    InetAddress ia = ias.nextElement();
                    if (ia instanceof Inet4Address) {
                        String tmp = ia.getHostAddress();
                        if(tmp.startsWith("192.168.")||tmp.startsWith("172.16")){
                            ip=tmp;
                        }
                    }
                }
            }
        }
        return ip;
    }

    public static void main(String[] args){
        System.out.println(getInnerIp());
    }
}
