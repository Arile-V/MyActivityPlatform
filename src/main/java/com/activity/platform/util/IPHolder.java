package com.activity.platform.util;

public class IPHolder {
    private static final ThreadLocal<String> LOCAL_IP = new ThreadLocal<>();
    public static void setIp(String ip) {
        LOCAL_IP.set(ip);
    }
    public static String getIp() {
        return LOCAL_IP.get();
    }
    public static void remove() {
        LOCAL_IP.remove();
    }
}
