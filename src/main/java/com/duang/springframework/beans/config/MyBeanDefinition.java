package com.duang.springframework.beans.config;

/**
 * @author duang
 * @date 2021-04-19
 * @Describe 存放元信息配置
 */
public class MyBeanDefinition {
    private String factoryBeanName; //beanName
    private String beanClassName;   //全类名
    private boolean isLazyInit=false; //是否延迟加载

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public boolean isLazyInit() {
        return isLazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        isLazyInit = lazyInit;
    }
}

