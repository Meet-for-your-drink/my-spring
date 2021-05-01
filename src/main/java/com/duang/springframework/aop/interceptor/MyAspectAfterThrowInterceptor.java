package com.duang.springframework.aop.interceptor;

import com.duang.springframework.aop.aspect.MyAbstractAspectJAdvice;
import com.duang.springframework.aop.aspect.MyJoinPoint;

import java.lang.reflect.Method;

/**
 * @author duang
 * @date 2021-04-30
 * @Describe
 */
public class MyAspectAfterThrowInterceptor extends MyAbstractAspectJAdvice implements MyMethodInterceptor{
    private MyJoinPoint joinPoint;

    private String throwName;
    public MyAspectAfterThrowInterceptor(Object newInstance, Method method) {
        super(newInstance,method);
    }

    @Override
    public Object invoke(MyMethodInvocation invocation) throws Throwable {
        try{
            return invocation.proceed();
        }catch(Throwable ex){
            joinPoint =invocation;
            invokeAdviceMethod(joinPoint,null,ex);
            throw ex;
        }
    }

    public void setThrowName(String aspectAfterThrowingName) {
        this.throwName = aspectAfterThrowingName;
    }
}
