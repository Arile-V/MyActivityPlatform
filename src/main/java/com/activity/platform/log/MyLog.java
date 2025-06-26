package com.activity.platform.log;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface MyLog {
    String method() default "";
    public boolean saveRequest() default true;
    public boolean saveResponse() default true;
}
