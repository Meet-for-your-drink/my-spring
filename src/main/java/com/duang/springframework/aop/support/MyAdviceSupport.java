package com.duang.springframework.aop.support;

import com.duang.springframework.aop.aspect.MyAdvice;
import com.duang.springframework.aop.config.MyAopConfig;
import com.duang.springframework.aop.interceptor.MyAspectAfterThrowInterceptor;
import com.duang.springframework.aop.interceptor.MyMethodAfterAdviceInterceptor;
import com.duang.springframework.aop.interceptor.MyMethodBeforeAdviceInterceptor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author duang
 * @date 2021-04-23
 * @Describe 解析AOP配置信息
 */
public class MyAdviceSupport {
    private MyAopConfig aopConfig;
    private Class targetClass;
    private Object target;
    private Pattern pointCutClassPattern ;

    //存储 方法和增强的关系
//    private Map<Method, Map<String, MyAdvice>> methodCache =  new HashMap<Method, Map<String, MyAdvice>>();
    private Map<Method, List<Object>> methodCache = new HashMap<Method, List<Object>>();

    public MyAdviceSupport(MyAopConfig aopConfig) {
        this.aopConfig = aopConfig;
    }

    public MyAopConfig getAopConfig() {
        return aopConfig;
    }

    public void setAopConfig(MyAopConfig aopConfig) {
        this.aopConfig = aopConfig;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    private void parse() {
        //public .* com.duang.demo.service..*Service..*(.*)
        //权限修饰符 方法返回值 包名.类名.方法名(参数列表)
        String pointCutRegex = this.aopConfig.getPointCut();
        //把Spring的Excpress变成Java能够识别的正则表达式
        String pointCut = pointCutRegex
                .replaceAll("\\.", "\\\\.")
                .replaceAll("\\\\.\\*", ".*")
                .replaceAll("\\(", "\\\\(")
                .replaceAll("\\)", "\\\\)");
        String pointCutForClassRegex = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);
        pointCutClassPattern = Pattern.compile("class "+pointCutForClassRegex.substring(pointCutForClassRegex.lastIndexOf(" ") + 1));
        //保存回调通知和目标切点之间的关系
        //query before() after()
        //add   before() after() afterThrowing()
        //先把切面方法存储起来，方便解析AOP配置文件的时候可以根据方法名快速找到对应的回调方法
        Map<String,Method> aspectMethods = new HashMap<String, Method>();
        try {
            Class aspectClass = Class.forName(this.aopConfig.getAspectClass());
            for(Method method:aspectClass.getMethods()){
                aspectMethods.put(method.getName(),method);
            }
            Pattern pointCutPattern = Pattern.compile(pointCutRegex);
            for(Method method:this.targetClass.getMethods()){
                String methodString = method.toString();
                //截去throws XXXException
                if(methodString.contains("throws")){
                    methodString = methodString.substring(0,methodString.lastIndexOf(" throws")).trim();
                }
                Matcher matcher = pointCutPattern.matcher(methodString);
                if(matcher.matches()){
                    List<Object> advices = new LinkedList<Object>();
                    if(!(null == this.aopConfig.getAspectBefore() || "".equals(this.aopConfig.getAspectBefore()))){
//                        advices.put("before",new MyAdvice(aspectClass.newInstance(),aspectMethods.get(this.getAopConfig().getAspectBefore())));
                        advices.add(new MyMethodBeforeAdviceInterceptor(aspectClass.newInstance(),aspectMethods.get(this.getAopConfig().getAspectBefore())));
                    }
                    if(!(null == this.aopConfig.getAspectAfter() || "".equals(this.aopConfig.getAspectAfter()))){
//                        advices.put("after",new MyAdvice(aspectClass.newInstance(),aspectMethods.get(this.getAopConfig().getAspectAfter())));
                        advices.add(new MyMethodAfterAdviceInterceptor(aspectClass.newInstance(),aspectMethods.get(this.getAopConfig().getAspectAfter())));
                    }
                    if(!(null == this.aopConfig.getAspectAfterThrow() || "".equals(this.aopConfig.getAspectAfterThrow()))){
                        MyAspectAfterThrowInterceptor advice = new MyAspectAfterThrowInterceptor(aspectClass.newInstance(),aspectMethods.get(this.getAopConfig().getAspectAfterThrow()));
                        advice.setThrowName(this.aopConfig.getAspectAfterThrowingName());
//                        advices.put("afterThrowing",advice);
                        advices.add(advice );
                    }
                    this.methodCache.put(method,advices);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public boolean pointCutMatch() {
        return this.pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }

//    public Map<String, MyAdvice> getAdvices(Method method, Class targetClass) throws NoSuchMethodException {
//        Map<String,MyAdvice> cache = this.methodCache.get(method);
//        if(null == cache || cache.isEmpty()){
//            Method m = targetClass.getMethod(method.getName(),method.getParameterTypes());
//            cache = methodCache.get(m);
//            this.methodCache.put(m,cache);
//        }
//        return cache;
//    }

    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class targetClass) throws Throwable {
        List<Object> cache = this.methodCache.get(method);
        if(null == cache || cache.isEmpty()){
            Method m = targetClass.getMethod(method.getName(),method.getParameterTypes());
            cache = methodCache.get(m);
            this.methodCache.put(m,cache);
        }
        return cache;
    }

}
