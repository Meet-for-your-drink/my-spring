package com.duang.springframework.aop.interceptor;

import com.duang.springframework.aop.aspect.MyAbstractAspectJAdvice;
import com.duang.springframework.aop.aspect.MyJoinPoint;

import java.lang.reflect.Method;

/**
 * @author duang
 * @date 2021-04-30
 * @Describe Before事件拦截
 */
public class MyMethodBeforeAdviceInterceptor extends MyAbstractAspectJAdvice implements MyMethodInterceptor {
    private MyJoinPoint joinPoint;
    public MyMethodBeforeAdviceInterceptor(Object aspect, Method adviceMethod) {
        super(aspect, adviceMethod);
    }

    @Override
    public Object invoke(MyMethodInvocation invocation) throws Throwable {
        joinPoint = invocation;
        this.invokeAdviceMethod(joinPoint,null,null);
        return invocation.proceed();
    }
}
