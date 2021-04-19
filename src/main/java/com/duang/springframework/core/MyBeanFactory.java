package com.duang.springframework.core;

/**
 * @author duang
 * @date 2021-04-19
 * @Describe 创建对象工厂的顶层接口
 */
public interface MyBeanFactory {
    Object getBean(Class beanClass);

    Object getBean(String beanName);
}
