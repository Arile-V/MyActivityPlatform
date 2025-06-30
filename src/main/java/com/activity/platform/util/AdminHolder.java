package com.activity.platform.util;

import com.activity.platform.pojo.Admin;

public class AdminHolder {
    private static ThreadLocal<Admin> adminThreadLocal;

    public static void save(Admin admin){
        adminThreadLocal.set(admin);
    }

    public static Admin get(){
        return adminThreadLocal.get();
    }

    public static void remove(){
        adminThreadLocal.remove();
    }
}
