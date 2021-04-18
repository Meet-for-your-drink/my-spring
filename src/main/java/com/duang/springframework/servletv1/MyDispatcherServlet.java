package com.duang.springframework.servletv1;

import com.duang.springframework.annotation.*;
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

/**
 * @author duang
 * @date 2021-04-18
 * @Describe
 */
public class MyDispatcherServlet extends HttpServlet {
    //上下文配置
    private Properties contextConfig = new Properties();
    //扫描包下的class
    private List<String> classNames = new ArrayList<String>();
    //ioc容器
    private Map<String,Object> ioc = new HashMap<String, Object>();
    //url-method
    private Map<String,Method> handleMapping = new HashMap<String, Method>();

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
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath,"").replaceAll("/+","/");
        if(!handleMapping.containsKey(url)){
            resp.getWriter().write("404 Not Found");
            return;
        }
        Method method = this.handleMapping.get(url);
        //获取方法所在的执行对象
        Object bean = ioc.get(CommonUtils.lowHead(method.getDeclaringClass().getSimpleName()));
        //获取方法参数列表,定位参数顺序
        //1.先把形参的位置和参数建立映射关系并且缓存下来
        Map<String,Integer> paramIndexMap = new HashMap<String, Integer>();
        Annotation[][] pa = method.getParameterAnnotations();
        //Annotation[i][j] 对应第i个参数的第j个注解.同一个参数不允许有多个相同注解,因此可以用Map<String,Integer>存放
        for(int i=0;i<pa.length;i++){
            for(Annotation annotation:pa[i]){
                if(annotation instanceof MyRequestParam){
                    String paramName = ((MyRequestParam) annotation).value();
                    if(!"".equals(paramName)){
                        paramIndexMap.put(paramName,i);
                    }
                }
            }
        }
        Class<?>[]  paramTypes = method.getParameterTypes();
        for(int i=0;i<paramTypes.length;i++){
            Class<?> type = paramTypes[i];
            if(type == HttpServletRequest.class || type == HttpServletResponse.class){
                paramIndexMap.put(type.getName(),i);
            }
        }
        //2.根据参数位置匹配参数名,从url中取到参数名字的对应值
        Object[] paramValues = new Object[paramTypes.length];
        //获取请求参数 xxx?name=a&name=b&name=c&age=19 => {(name,[a,b,c],(age,[19]))}
        Map<String,String[]> params = req.getParameterMap();
        for(Map.Entry<String,String[]> param:params.entrySet()){
            String value = Arrays.toString(param.getValue())
                    .replaceAll("\\[|\\]","")
                    .replaceAll("\\s","");
            if(!paramIndexMap.containsKey(param.getKey())){
                continue;
            }
            int index = paramIndexMap.get(param.getKey());
            //涉及到强制类型转换,稍后处理
            paramValues[index] = value;
        }
        if(paramIndexMap.containsKey(HttpServletRequest.class.getName())){
            int index = paramIndexMap.get(HttpServletRequest.class.getName());
            paramValues[index]=req;
        }
        if(paramIndexMap.containsKey(HttpServletResponse.class.getName())){
            int index = paramIndexMap.get(HttpServletResponse.class.getName());
            paramValues[index]=resp;
        }
        //3.组成动态实际参数列表，传给反射调用
        method.invoke(bean ,paramValues);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //加载配置文件.读取web.xml中<init-param>中的配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));
        //扫描相关类
        doScan(contextConfig.getProperty("scanPackage"));
        //初始化IOC容器，将扫描到的类进行实例化，缓存到IOC容器中
        doInstance();
        //完成依赖注入
        try {
            doAutowired();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //初始化HandlerMapping
        doInitHandlerMapping();

        System.out.println("springframework is init");
    }

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
    //扫描classpath下符合扫描包路径规则下的class文件
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
                classNames.add(className);
            }
        }
    }

    private void doInstance() {
        if(classNames.isEmpty()){
            return;
        }
        for(String className: classNames){
            try {
                Class<?> clazz = Class.forName(className);
                //非注解了@MyController和@Service不进行实例化
                if(clazz.isAnnotationPresent(MyController.class)){
                    //以类名为key,存入ioc容器中,beanName=类名首字母小写
                    String beanName = CommonUtils.lowHead(clazz.getSimpleName());
                    Object instance = clazz.newInstance();
                    ioc.put(beanName,instance);
                }else if(clazz.isAnnotationPresent(MyService.class)){
                    //以类名为key,存入ioc容器中,beanName=类名首字母小写
                    String beanName = CommonUtils.lowHead(clazz.getSimpleName());
                    Object instance = clazz.newInstance();
                    //如果在多包下出现了相同的类名,优先使用别名
                    MyService myService = clazz.getAnnotation(MyService.class);
                    if(!"".equals(myService.value())){
                        beanName = myService.value();
                    }
                    ioc.put(beanName,instance);
                    //如果是接口实现类,是接口名为key,实现类为value存入ioc容器中
                    Class[] interfaces = clazz.getInterfaces();
                    for(Class classInterface : interfaces){
                        if(ioc.containsKey(classInterface.getSimpleName())){
                            throw new Exception("The "+classInterface.getSimpleName()+" is exists,please use alias");
                        }
                        ioc.put(CommonUtils.lowHead(classInterface.getSimpleName()),instance);
                    }
                }else{
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void doAutowired() throws Exception{
        if(ioc.isEmpty()){
            return;
        }
        for(Map.Entry<String,Object> entry:ioc.entrySet()){
            Object bean = entry.getValue();
            //忽略字段的修饰符,获取属性,注解@MyAutowired的进行属性注入
            for(Field declareField : bean.getClass().getDeclaredFields()){
                if(!declareField.isAnnotationPresent(MyAutowired.class)){
                    continue;
                }
                //强制访问
                declareField.setAccessible(true);
                String beanName = declareField.getAnnotation(MyAutowired.class).value().trim() == ""?
                        CommonUtils.lowHead(declareField.getClass().getSimpleName()): declareField.getAnnotation(MyAutowired.class).value();
                if(ioc.containsKey(beanName)){
                    declareField.set(bean,ioc.get(beanName));
                }else{
                    throw new Exception("miss "+beanName+" on "+bean.getClass().getName()+"."+ declareField.getName());
                }
            }
        }
    }

    private void doInitHandlerMapping() {
        if(ioc.isEmpty()){
            return;
        }
        for(Object instance : ioc.values()){
            if(instance.getClass().isAnnotationPresent(MyController.class)){
                String baseUrl = instance.getClass().getAnnotation(MyRequestMapping.class).value();
                Method[] methods = instance.getClass().getMethods();
                //只迭代public方法
                for(Method method: methods){
                    if(method.isAnnotationPresent(MyRequestMapping.class)){
                        String url = ("/"+baseUrl+"/"+method.getAnnotation(MyRequestMapping.class).value()).replaceAll("/+","/");
                        handleMapping.put(url,method);
                    }
                }
            }
        }
    }
}
