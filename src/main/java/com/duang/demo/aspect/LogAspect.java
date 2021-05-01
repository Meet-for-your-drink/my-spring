package com.duang.demo.aspect;

import com.duang.springframework.aop.aspect.MyJoinPoint;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Tom.
 */
@Slf4j
public class LogAspect {

    //在调用一个方法之前，执行before方法
    public void before(MyJoinPoint joinPoint){
        joinPoint.setUserAttribute("startTime_" + joinPoint.getMethod().getName(),System.currentTimeMillis());
        log.info("Invoker Before Method!!!");
    }
    //在调用一个方法之后，执行after方法
    public void after(MyJoinPoint joinPoint){
        long startTime = (Long)joinPoint.getUserAttribute("startTime_"+joinPoint.getMethod().getName());
        long endTime = System.currentTimeMillis();
        log.info("Invoker After Method!!!"+"use time:"+(endTime-startTime) );
    }

    public void afterThrowing(MyJoinPoint joinPoint){
        log.info("出现异常");
    }
}
