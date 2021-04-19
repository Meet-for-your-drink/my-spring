package com.duang.springframework.context;

import com.duang.springframework.annotation.MyAutowired;
import com.duang.springframework.annotation.MyController;
import com.duang.springframework.annotation.MyService;
import com.duang.springframework.beans.config.MyBeanDefinition;
import com.duang.springframework.beans.support.MyBeanDefinitionReader;
import com.duang.springframework.beans.support.MyBeanWrapper;
import com.duang.springframework.core.MyBeanFactory;
import com.duang.springframework.beans.support.MyDefaultListableBeanFactory;
import com.duang.springframework.utils.CommonUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author duang
 * @date 2021-04-19
 * @Describe
 */
public class MyApplicationContext implements MyBeanFactory {

    private MyDefaultListableBeanFactory registry = new MyDefaultListableBeanFactory();

    private MyBeanDefinitionReader reader = null;

    //三级缓存
    private Map<String,MyBeanWrapper> factoryBeanInstanceCache = new HashMap<String, MyBeanWrapper>();

    //存放原生对象
    private Map<String,Object> factoryBeanObjectCache = new HashMap<String, Object>();

    public MyApplicationContext(String ... configLocations) {
        //加载配置文件
        reader = new MyBeanDefinitionReader(configLocations);
        //解析配置文件，将所有配置信息封装为MyBeanDefinition对象
        List<MyBeanDefinition> beanDefinitions =  reader.loadBeanDefinition();
        //所有配置信息缓存起来
        try {
            this.registry.doRegistBeanDefinition(beanDefinitions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //加载非延时加载的所有bean
        doLoadInstance();
    }

    private void doLoadInstance() {
        //循环调用getBean方法
        for(Map.Entry<String,MyBeanDefinition> entry: this.registry.beanDefinitionMap.entrySet()){
            String beanName = entry.getKey();
            if(entry.getValue().isLazyInit()){
                continue;
            }
            this.getBean(beanName);
        }
    }

    @Override
    public Object getBean(Class beanClass) {
        return getBean(CommonUtils.lowHead(beanClass.getSimpleName()));
    }
    //从IOC容器中获得bean对象
    @Override
    public Object getBean(String beanName) {
        //从registry中获取到BeanDefinition
        MyBeanDefinition definition = this.registry.beanDefinitionMap.get(beanName);
        //反射进行实例化
        Object instance = instantiateBean(beanName,definition);
        //将返回的bean对象封装成BeanWrapper
        MyBeanWrapper beanWrapper = new MyBeanWrapper(instance);
        //执行依赖注入
        try {
            populateBean(beanName,definition,beanWrapper);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //保存到IOC容器中
        this.factoryBeanInstanceCache.put(beanName,beanWrapper);
        return beanWrapper.getWrappedInstance();
    }

    private void populateBean(String beanName, MyBeanDefinition definition, MyBeanWrapper beanWrapper) throws Exception {
        Object instance = beanWrapper.getWrappedInstance();
        Class<?> clazz = beanWrapper.getWrappedClass();
        if(!(clazz.isAnnotationPresent(MyController.class)|| clazz.isAnnotationPresent(MyService.class))){
            return;
        }
        for(Field declareField : clazz.getDeclaredFields()){
            if(!declareField.isAnnotationPresent(MyAutowired.class)){
                continue;
            }
            //强制访问
            declareField.setAccessible(true);
            String autowireBeanName = declareField.getAnnotation(MyAutowired.class).value().trim() == ""?
                    CommonUtils.lowHead(declareField.getClass().getSimpleName()): declareField.getAnnotation(MyAutowired.class).value();
            if(this.factoryBeanInstanceCache.containsKey(autowireBeanName)){
                declareField.set(instance,this.factoryBeanInstanceCache.get(autowireBeanName).getWrappedInstance());
            }else{
                throw new Exception("miss "+beanName+" on "+instance.getClass().getName()+"."+ declareField.getName());
            }
        }
    }

    private Object instantiateBean(String beanName, MyBeanDefinition definition) {
        String className = definition.getBeanClassName();
        Object instance = null;
        try {
            instance = Class.forName(className).newInstance();
            //如果是代理对象，触发AOP

            this.factoryBeanObjectCache.put(beanName,instance);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public int getBeanDefinitionCount(){
         return this.registry.beanDefinitionMap.size();
    }

    public String[] getBeanDefinitionNames(){
        return this.registry.beanDefinitionMap.keySet().toArray(new String[0]);
    }


}
