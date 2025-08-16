package com.activity.platform.util;

import com.activity.platform.dto.UserDTO;
public class UserHolder {
    private static ThreadLocal<UserDTO> userThreadLocal = new ThreadLocal<>();

    public static void setUser(UserDTO user) {
        userThreadLocal.set(user);
    }

    public static UserDTO getUser() {
        return userThreadLocal.get();
    }

    public static boolean exists() {
        return userThreadLocal != null;
    }

    public static void remove() {
        userThreadLocal.remove();
    }
}
