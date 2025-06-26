package com.activity.platform.util;

import cn.hutool.core.util.RandomUtil;

public class EmailCode {
    public static String randomCode(){
        StringBuilder code = new StringBuilder();
        for(int i = 0;i<6;i++){
            code.append((int) (RandomUtil.randomInt(0, 9)));
        }
        return code.toString();
    }
}
