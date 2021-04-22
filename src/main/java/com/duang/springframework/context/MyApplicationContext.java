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
import java.util.*;

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

    //一级缓存：保存成熟的bean
    private Map<String,Object> singletonObjects = new HashMap<String, Object>();

    //二级缓存，存放早期纯净的bean
    private Map<String,Object> earlySingletonObjects = new HashMap<String, Object>();

    //循环依赖的标志，当前正在创建的BeanName，Mark一下
    private Set<String> singletonCurrentlyInCreation = new HashSet<String>();


    //存放原生对象
    private Map<String,Object> factoryBeanObjectCache = new HashMap<String, Object>();

    //存放类和实例,保证单例
    private Map<String,Object> instantiateBeanMap = new HashMap<String,Object>();

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

        //从一级缓存中获取Bean
        Object singleton = getSingleton(beanName,definition);
        if(null != singleton){
            return singleton;
        }
        //标记bean正在创建
        if(!singletonCurrentlyInCreation.contains(beanName)){
            singletonCurrentlyInCreation.add(beanName);
        }

        //反射进行实例化
        Object instance = instantiateBean(beanName,definition);

        //实例化完成先存入一级缓存
        this.singletonObjects.put(beanName,instance);


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

    private Object getSingleton(String beanName, MyBeanDefinition definition) {
        //先去一级缓存获取
        Object bean = singletonObjects.get(beanName);
        //如果一级缓存中没有,但是又有创建标识，说明存在循环依赖
        if(null == bean && this.singletonCurrentlyInCreation.contains(beanName)){
            bean = this.earlySingletonObjects.get(beanName);
            //二级缓存中也不存在时从三级缓存获取
            if(null == bean){
                //****instantiateBean放入的不是三级缓存但最终的对象会被放入三级缓存
                bean = instantiateBean(beanName,definition);
                //创建出来的对象放入二级缓存中
                earlySingletonObjects.put(beanName,bean);
            }
        }
        return bean;
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
//                declareField.set(instance,this.factoryBeanInstanceCache.get(autowireBeanName).getWrappedInstance());
                declareField.set(instance,getBean(autowireBeanName));
            }else{
                throw new Exception("miss "+beanName+" on "+instance.getClass().getName()+"."+ declareField.getName());
            }
        }
    }

    private Object instantiateBean(String beanName, MyBeanDefinition definition) {
        String className = definition.getBeanClassName();
        Object instance = null;
        try {
            if(instantiateBeanMap.containsKey(className)){
                instance = instantiateBeanMap.get(className);
            }else{
                instance = Class.forName(className).newInstance();
            }
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


    public Properties getConfig() {
        return this.reader.getConfig();
    }
}
