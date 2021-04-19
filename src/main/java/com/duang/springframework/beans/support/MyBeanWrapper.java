package com.duang.springframework.beans.support;

/**
 * @author duang
 * @date 2021-04-19
 * @Describe
 */
public class MyBeanWrapper {
    private Object wrappedInstance;
    private Class<?> wrappedClass;

    public MyBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
        this.wrappedClass = wrappedInstance.getClass();
    }

    public Object getWrappedInstance(){
        return this.wrappedInstance;
    }

    public Class<?> getWrappedClass(){
        return this.wrappedClass;
    }

}
