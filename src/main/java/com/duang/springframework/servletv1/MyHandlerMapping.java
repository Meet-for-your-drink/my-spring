package com.duang.springframework.servletv1;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @author duang
 * @date 2021-04-21
 * @Describe 自定义HandlerMapping
 */
public class MyHandlerMapping {
    //优化，url支持正则表达式
    protected Pattern urlPattern;
    private Method method;
    private Object controller;

    public MyHandlerMapping(Pattern urlPattern, Method method, Object controller) {
        this.urlPattern = urlPattern;
        this.method = method;
        this.controller = controller;
    }

    public Pattern getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(Pattern urlPattern) {
        this.urlPattern = urlPattern;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }
}
