package com.duang.springframework.annotation;

import java.lang.annotation.*;

/**
 * @author duang
 * @date 2021-04-18
 * @Describe
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestMapping {
    String value() default "";
}
