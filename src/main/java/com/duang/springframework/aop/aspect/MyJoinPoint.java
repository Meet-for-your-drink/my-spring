package com.duang.springframework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author duang
 * @date 2021-04-30
 * @Describe
 */
public interface MyJoinPoint {
    //获取目标类的实例
    Object getThis();

    //获取执行参数
    Object[] getArguments();

    //获取执行方法
    Method getMethod();

    void setUserAttribute(String key, Object value);

    Object getUserAttribute(String key);
}
