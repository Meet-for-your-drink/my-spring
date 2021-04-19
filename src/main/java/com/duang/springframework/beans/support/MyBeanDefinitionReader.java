package com.duang.springframework.beans.support;

import com.duang.springframework.beans.config.MyBeanDefinition;
import com.duang.springframework.utils.CommonUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author duang
 * @date 2021-04-19
 * @Describe 读取配置文件
 */
public class MyBeanDefinitionReader {
    //保存用户配置好的配置文件
    private Properties contextConfig = new Properties();

    //缓存从包路径下扫描的全类名，需要被注册的BeanClass
    private List<String> registryBeanClass = new ArrayList<String>();

    public MyBeanDefinitionReader(String ... configLocations) {
        //加载配置文件
        doLoadConfig(configLocations[0]);
        //扫描包路径
        doScan(contextConfig.getProperty("scanPackage"));
    }

    public List<MyBeanDefinition> loadBeanDefinition(){
        List<MyBeanDefinition> result = new ArrayList<MyBeanDefinition>();
        for(String className: registryBeanClass){
            try {
                Class<?> beanClass = Class.forName(className);
                if(beanClass.isInterface()){
                    continue;
                }
                //默认类名首字母小写
                result.add(doCreateBeanDefinition(CommonUtils.lowHead(beanClass.getSimpleName()),beanClass.getName()));
                //接口实现类
                for(Class<?> i : beanClass.getInterfaces()){
                    result.add(doCreateBeanDefinition(CommonUtils.lowHead(i.getSimpleName()),beanClass.getName()));
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    //创建BeanDefinition
    private MyBeanDefinition doCreateBeanDefinition(String lowHead, String name) {
        MyBeanDefinition beanDefinition = new MyBeanDefinition();
        beanDefinition.setFactoryBeanName(lowHead);
        beanDefinition.setBeanClassName(name);
        return beanDefinition;
    }

    //加载资源文件
    private void doLoadConfig(String contextConfigLocation) {
        //读取资源文件转为字节流
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            //加载配置
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(null != is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    //扫描classpath下符合扫描包路径规则下的class文件，获得类的全路径名
    private void doScan(String scanPackage) {
        //com.duang.demo => /com/duang/demo 对应target-classes目录
        URL url = this.getClass().getClassLoader().getResource("/"+scanPackage.replaceAll("\\.","/"));
        File classPath = new File(url.getFile());
        for(File file: classPath.listFiles()){
            if(file.isDirectory()){
                doScan(scanPackage+"."+file.getName());
            }else{
                if(!file.getName().endsWith(".class")) {
                    continue;
                }
                String className = scanPackage+"."+file.getName().replace(".class","");
                registryBeanClass.add(className);
            }
        }
    }
}
