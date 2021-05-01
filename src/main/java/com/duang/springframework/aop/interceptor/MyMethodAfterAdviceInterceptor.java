package com.duang.springframework.aop.interceptor;

import com.duang.springframework.aop.aspect.MyAbstractAspectJAdvice;
import com.duang.springframework.aop.aspect.MyJoinPoint;

import java.lang.reflect.Method;

/**
 * @author duang
 * @date 2021-04-30
 * @Describe After事件拦截
 */
public class MyMethodAfterAdviceInterceptor extends MyAbstractAspectJAdvice implements MyMethodInterceptor{
    private MyJoinPoint joinPoint;
    public MyMethodAfterAdviceInterceptor(Object newInstance, Method method) {
        super(newInstance,method);
    }

    @Override
    public Object invoke(MyMethodInvocation invocation) throws Throwable {
        joinPoint = invocation;
        Object returnVal = invocation.proceed();
        this.invokeAdviceMethod(joinPoint,returnVal,null);
        return returnVal;
    }
}
