package com.duang.springframework.aop.interceptor;

import org.omg.PortableInterceptor.Interceptor;

/**
 * @author duang
 * @date 2021-04-30
 * @Describe 方法拦截解耦
 */
public interface MyMethodInterceptor {
    Object invoke(MyMethodInvocation invocation) throws Throwable;
}
