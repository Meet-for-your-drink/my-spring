package com.duang.springframework.aop;

import com.duang.springframework.aop.support.MyAdviceSupport;

/**
 * @author duang
 * @date 2021-04-23
 * @Describe 代理工厂，用于生成代理对象
 */
public class MyDefaultAopProxyFactory {
    public MyAopProxy createAopProxy(MyAdviceSupport adviceSupport) {
        Class targetClass = adviceSupport.getTargetClass();
        if(targetClass.getInterfaces().length > 0){
            //有实现接口使用JdkDynamicAopProxy
            return new MyJdkDynamicAopProxy(adviceSupport);
        }else{
            return new MyCglibAopPorxy(adviceSupport);
        }
    }
}
