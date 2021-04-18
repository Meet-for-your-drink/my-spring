package com.duang.springframework.annotation;

import java.lang.annotation.*;

/**
 * @author duang
 * @date 2021-04-18
 * @Describe 自定义controller注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyController {
    String value() default "";
}
