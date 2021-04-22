package com.duang.springframework.servletv1;

import com.duang.springframework.annotation.*;
import com.duang.springframework.context.MyApplicationContext;
import com.duang.springframework.utils.CommonUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author duang
 * @date 2021-04-18
 * @Describe
 */
public class MyDispatcherServlet extends HttpServlet {

    private MyApplicationContext applicationContext = null;

    //HandlerMapping容器
    private List<MyHandlerMapping> handlerMappings = new ArrayList<MyHandlerMapping>();
    //HandlerAdapter 参数适配器容器
    private Map<MyHandlerMapping,MyHandlerAdapter> handlerAdapters = new HashMap<MyHandlerMapping,MyHandlerAdapter>();
    //ViewResolver视图解析器容器
    private List<MyViewResolver> viewResolvers = new ArrayList<MyViewResolver>();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //根据URL委派给具体的调用方法
        try {
            doDispatch(req,resp);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String,Object> details = new HashMap<String, Object>();
            MyModelAndView mv = new MyModelAndView("500");
            details.put("detail","500 Exception,Details:");
            details.put("stackTrace",Arrays.toString(e.getStackTrace()));
            mv.setModel(details);
            processDispatchResult(req,resp,mv);
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws InvocationTargetException, IllegalAccessException {
        //根据url获取handlermapping
        MyHandlerMapping handlerMapping = getHandler(req);
        //根据HandlerMapping获取HandlerAdapter
        if(handlerMapping == null){
            processDispatchResult(req,resp,new MyModelAndView("404"));
            return;
        }
        MyHandlerAdapter handlerAdapter = getHandlerAdapter(handlerMapping);
        //根据HandlerAdapter获取对应的ModelAndView
        MyModelAndView mv = handlerAdapter.handler(req,resp,handlerMapping);
        //根据ViewResolver找到对应的View对象
        //通过View对象渲染页面，并返回
        processDispatchResult(req,resp,mv);
    }

    private MyHandlerAdapter getHandlerAdapter(MyHandlerMapping handlerMapping) {
        if(this.handlerAdapters.isEmpty()){
            return null;
        }
        return this.handlerAdapters.get(handlerMapping);
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, MyModelAndView mv) {
        if (mv == null) {
            return;
        }
        if(this.viewResolvers.isEmpty()){
            return;
        }
        for(MyViewResolver viewResolver:this.viewResolvers){
            MyView view = viewResolver.resolveViewName(mv.getViewName());
            try {
                view.render(mv.getModel(),req,resp);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
    }

    private MyHandlerMapping getHandler(HttpServletRequest req) {
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath,"").replaceAll("/+","/");
        for(MyHandlerMapping handlerMapping : handlerMappings){
            if(handlerMapping.getUrlPattern().matcher(url).matches()){
                return handlerMapping;
            }
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        applicationContext = new MyApplicationContext(config.getInitParameter("contextConfigLocation"));
        //初始化策略
        initStrategies(applicationContext);
        System.out.println("springframework is init");
    }

    //初始化策略
    protected void initStrategies(MyApplicationContext context){
        //多文件上传组件
//        initMultipartResolver(context);
        //初始化本地语言环境
//        initLocaleResolver(context);
        //初始化模板处理器
//        initThemeResolver(context);
        //handlerMapping
        initHandlerMappings(context);
        //初始化参数适配器
        initHandlerAdapters(context);
        //初始化异常拦截器
//        initHandlerExceptionResolvers(context);
        //初始化视图预处理器
//        initRequestToViewNameTranslator(context);
        //初始化视图转换器
        initViewResolvers(context);
        //初始化FlashMap管理器
//        initFlashMapManager(context);
    }

    private void initViewResolvers(MyApplicationContext context) {
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File rootPath = new File(templateRootPath);
        if(rootPath.listFiles().length != 0){
            this.viewResolvers.add(new MyViewResolver(templateRoot));
        }
    }

    private void getFileWithPath(File rootPath){
        for(File file:rootPath.listFiles()){
            if(file.isDirectory()){
                getFileWithPath(file);
            }
            this.viewResolvers.add(new MyViewResolver(file.getPath()));
        }
    }

    private void initHandlerAdapters(MyApplicationContext context) {
        for(MyHandlerMapping handlerMapping:handlerMappings){
            handlerAdapters.put(handlerMapping,new MyHandlerAdapter());
        }
    }

    private void initHandlerMappings(MyApplicationContext context) {
        if(applicationContext.getBeanDefinitionCount() == 0){
            return;
        }
        for(String name : applicationContext.getBeanDefinitionNames()){
            Object instance = applicationContext.getBean(name);
            if(instance.getClass().isAnnotationPresent(MyController.class)){
                String baseUrl = instance.getClass().getAnnotation(MyRequestMapping.class).value();
                Method[] methods = instance.getClass().getMethods();
                //只迭代public方法
                for(Method method: methods){
                    if(method.isAnnotationPresent(MyRequestMapping.class)){
                        //url正则表达式匹配
                        String regex = ("/"+baseUrl+"/"+method.getAnnotation(MyRequestMapping.class).value())
                                .replaceAll("\\*",".*")
                                .replaceAll("/+","/");
                        Pattern pattern = Pattern.compile(regex);
                        handlerMappings.add(new MyHandlerMapping(pattern,method,instance));
                    }
                }
            }
        }
    }
}
