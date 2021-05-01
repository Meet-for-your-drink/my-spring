package com.duang.springframework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author duang
 * @date 2021-04-25
 * @Describe 增强
 */
public class MyAdvice {
    private Object aspect;
    private Method adviceMethod;
    private String throwName;

    public MyAdvice(Object aspect, Method adviceMethod) {
        this.aspect = aspect;
        this.adviceMethod = adviceMethod;
    }

    public Object getAspect() {
        return aspect;
    }

    public void setAspect(Object aspect) {
        this.aspect = aspect;
    }

    public Method getAdviceMethod() {
        return adviceMethod;
    }

    public void setAdviceMethod(Method adviceMethod) {
        this.adviceMethod = adviceMethod;
    }

    public String getThrowName() {
        return throwName;
    }

    public void setThrowName(String throwName) {
        this.throwName = throwName;
    }
}
