package com.duang.demo.controller;

import com.duang.demo.service.IHelloService;
import com.duang.springframework.annotation.MyAutowired;
import com.duang.springframework.annotation.MyController;
import com.duang.springframework.annotation.MyRequestMapping;
import com.duang.springframework.annotation.MyRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author duang
 * @date 2021-04-18
 * @Describe
 */
@MyController
@MyRequestMapping(value = "/hello")
public class HelloController {
    @MyAutowired
    IHelloService IHelloService;

    @MyRequestMapping(value="/say")
    public void sayHello(HttpServletRequest req, HttpServletResponse resp, @MyRequestParam(value="name") String name){
        String result = IHelloService.sayHello(name);
        try {
            resp.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
