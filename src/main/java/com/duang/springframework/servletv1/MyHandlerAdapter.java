package com.duang.springframework.servletv1;

import com.duang.springframework.annotation.MyRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author duang
 * @date 2021-04-21
 * @Describe
 */
public class MyHandlerAdapter {
    public MyModelAndView handler(HttpServletRequest req, HttpServletResponse resp, MyHandlerMapping handlerMapping) throws InvocationTargetException, IllegalAccessException {
        Method method = handlerMapping.getMethod();
        //获取方法参数列表,定位参数顺序
        //1.先把形参的位置和参数建立映射关系并且缓存下来
        Map<String,Integer> paramIndexMap = new HashMap<String, Integer>();
        Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
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
        Class<?>[]  paramTypes = handlerMapping.getMethod().getParameterTypes();
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
            paramValues[index] = caseStringValue(value,paramTypes[index]);
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
        Object result = method.invoke(handlerMapping.getController(),paramValues);
        //封装结果到视图模型
        if(null == result || result instanceof Void){
            return null;
        }
        boolean isModelAndView = handlerMapping.getMethod().getReturnType() == MyModelAndView.class;
        if(isModelAndView){
            return (MyModelAndView)result;
        }
        return null;
    }

    //参数类型转化
    private Object caseStringValue(String value, Class<?> paramType) {
        if(String.class == paramType){
            return value;
        }
        if(Integer.class == paramType){
            return Integer.valueOf(value);
        }else{
            if(value != null){
                return value;
            }
            return null;
        }

    }
}
