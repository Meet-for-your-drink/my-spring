package com.duang.springframework.servletv1;

import java.util.Map;

/**
 * @author duang
 * @date 2021-04-22
 * @Describe 视图模型
 */
public class MyModelAndView {
    private String viewName;
    private Map<String,Object> model;

    public MyModelAndView(String s) {
        this.viewName = s;
    }

    public MyModelAndView(String viewName, Map<String, Object> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }

    public void setModel(Map<String, Object> model) {
        this.model = model;
    }
}
