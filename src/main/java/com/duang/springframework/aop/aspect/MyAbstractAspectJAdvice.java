package com.duang.springframework.aop.aspect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author duang
 * @date 2021-05-01
 * @Describe
 */
public class MyAbstractAspectJAdvice {
    //增强类实例
    private Object aspect;
    //增强方法
    private Method adviceMethod;
    private String throwName;

    public MyAbstractAspectJAdvice(Object aspect, Method adviceMethod) {
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

    protected Object invokeAdviceMethod(MyJoinPoint joinPoint, Object returnVal, Throwable ex) throws InvocationTargetException, IllegalAccessException {
        Class<?>[] paramTypes = this.adviceMethod.getParameterTypes();
        if(null == paramTypes || paramTypes.length == 0){
            return this.adviceMethod.invoke(aspect);
        }else{
            Object[] args = new Object[paramTypes.length];
            for(int i=0;i<paramTypes.length;i++){
                if(paramTypes[i] == MyJoinPoint.class){
                    args[i] = joinPoint;
                }else if(paramTypes[i] == Throwable.class){
                    args[i] = ex;
                }else if(paramTypes[i] == Object.class){
                    args[i] = returnVal;
                }
            }
            return this.adviceMethod.invoke(aspect,args);
        }
    }
}
