package com.duang.springframework.aop.interceptor;

import com.duang.springframework.aop.aspect.MyJoinPoint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author duang
 * @date 2021-04-30
 * @Describe
 */
public class MyMethodInvocation implements MyJoinPoint {
    protected final Object proxy;
    protected final Object target;
    protected final Method method;
    protected Object[] arguments;
    private final Class<?> targetClass;
    protected final List<?> interceptorsAndDynamicMethodMatchers;

    private int currentInterceptorIndex = -1;

    private Map<String,Object> userAttributes = new HashMap<String,Object>();

    public MyMethodInvocation(Object proxy, Object target, Method method, Object[] arguments, Class targetClass, List<Object> interceptorsAndDynamicMethodMatchers) {
        this.proxy = proxy;
        this.target = target;
        this.method = method;
        this.arguments = arguments;
        this.targetClass = targetClass;
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }

    public Object proceed() throws Throwable {
        if(this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size()-1){
            //调用被增强方法
            return invokeJoinPoint();
        }
        Object advice = this.interceptorsAndDynamicMethodMatchers.get(++currentInterceptorIndex);
        if(advice instanceof MyMethodInterceptor){
            MyMethodInterceptor methodInterceptor = (MyMethodInterceptor)advice;
            methodInterceptor.invoke(this);
        }else{
            return proceed();
        }

        return null;
    }

    private Object invokeJoinPoint() throws Exception {
        return this.method.invoke(targetClass,arguments);
    }

    public Object getProxy() {
        return proxy;
    }

    public Object getTarget() {
        return target;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public void setUserAttribute(String key, Object value) {
        this.userAttributes.put(key,value);
    }

    @Override
    public Object getUserAttribute(String key) {
        return this.userAttributes.get(key);
    }

    @Override
    public Object getThis() {
        return this;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public List<?> getInterceptorsAndDynamicMethodMatchers() {
        return interceptorsAndDynamicMethodMatchers;
    }

    public int getCurrentInterceptorIndex() {
        return currentInterceptorIndex;
    }

    public void setCurrentInterceptorIndex(int currentInterceptorIndex) {
        this.currentInterceptorIndex = currentInterceptorIndex;
    }
}
