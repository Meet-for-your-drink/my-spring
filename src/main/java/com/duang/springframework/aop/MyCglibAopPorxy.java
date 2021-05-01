package com.duang.springframework.aop;

import com.duang.springframework.aop.support.MyAdviceSupport;

/**
 * @author duang
 * @date 2021-04-23
 * @Describe
 */
public class MyCglibAopPorxy implements MyAopProxy{
    private MyAdviceSupport adviceSupport;

    public MyCglibAopPorxy(MyAdviceSupport adviceSupport) {
        this.adviceSupport = adviceSupport;
    }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
