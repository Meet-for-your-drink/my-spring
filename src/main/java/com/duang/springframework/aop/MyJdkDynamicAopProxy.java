package com.duang.springframework.aop;

import com.duang.springframework.aop.aspect.MyAdvice;
import com.duang.springframework.aop.interceptor.MyMethodInvocation;
import com.duang.springframework.aop.support.MyAdviceSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

/**
 * @author duang
 * @date 2021-04-23
 * @Describe
 */
public class MyJdkDynamicAopProxy implements MyAopProxy, InvocationHandler {
    private MyAdviceSupport adviceSupport;

    public MyJdkDynamicAopProxy(MyAdviceSupport adviceSupport) {
        this.adviceSupport = adviceSupport;
    }

    @Override
    public Object getProxy() {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(),
                this.adviceSupport.getTargetClass().getInterfaces(),
                this);
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //构建执行链
        List<Object> chain = this.adviceSupport
                .getInterceptorsAndDynamicInterceptionAdvice(method, this.adviceSupport.getTargetClass());

        MyMethodInvocation mi = new MyMethodInvocation(
                proxy,
                this.adviceSupport.getTarget(),
                method,
                args,
                this.adviceSupport.getTargetClass(),
                chain
        );
        return mi.proceed();
//        Map<String, MyAdvice> advices = this.adviceSupport.getAdvices(method,
//                this.adviceSupport.getTargetClass());
//
//        Map<String, MyAdvice> advices = null;
//        try {
//            advices = this.adviceSupport.getAdvices(method,this.adviceSupport.getTargetClass());
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }
//        invokeAdvice(advices.get("before"));
//        Object returnValue = null;
//        try {
//            returnValue = method.invoke(this.adviceSupport.getTarget(),args);
//        } catch (Exception e) {
//            e.printStackTrace();
//            invokeAdvice(advices.get("afterThrowing"));
//        }
//        invokeAdvice(advices.get("after"));
//        return returnValue;
    }

    private void invokeAdvice(MyAdvice advice){
        try {
            advice.getAdviceMethod().invoke(advice.getAspect());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}
