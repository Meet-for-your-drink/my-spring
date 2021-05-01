package com.duang.springframework.aop;

/**
 * @author duang
 * @date 2021-04-23
 * @Describe
 */
public interface MyAopProxy {
    Object getProxy();

    Object getProxy(ClassLoader classLoader);
}
