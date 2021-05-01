package com.duang.demo.service.impl;

import com.duang.springframework.annotation.MyService;

/**
 * @author duang
 * @date 2021-04-18
 * @Describe
 */
@MyService
public class HelloService implements com.duang.demo.service.IHelloService {
    @Override
    public String sayHello(String name) {
        return name+",hello";
    }
}
