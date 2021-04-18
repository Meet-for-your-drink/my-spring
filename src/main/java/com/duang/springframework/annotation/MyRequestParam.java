package com.duang.springframework.annotation;

import java.lang.annotation.*;

/**
 * @author duang
 * @date 2021-04-18
 * @Describe
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestParam {
    String value() default "";
}
