package com.duang.springframework.beans.support;

import com.duang.springframework.beans.config.MyBeanDefinition;
import com.duang.springframework.core.MyBeanFactory;
import com.duang.springframework.utils.CommonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author duang
 * @date 2021-04-19
 * @Describe
 */
public class MyDefaultListableBeanFactory implements MyBeanFactory {
    public Map<String,MyBeanDefinition> beanDefinitionMap = new HashMap<String,MyBeanDefinition>();
    @Override
    public Object getBean(Class beanClass) {
        return null;
    }

    @Override
    public Object getBean(String beanName) {
        return null;
    }

    public void doRegistBeanDefinition(List<MyBeanDefinition> beanDefinitions) throws Exception {
        for(MyBeanDefinition beanDefinition:beanDefinitions){
            if(this.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())){
                throw new Exception("The "+beanDefinition.getFactoryBeanName()+" is exists,please use alias");
            }
            beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
        }
    }
}
