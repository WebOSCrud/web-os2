package cn.donting.web.os.web.ioc;

import cn.donting.web.os.web.annotation.Autowired;
import cn.donting.web.os.web.annotation.Properties;
import cn.donting.web.os.web.annotation.RestController;
import cn.donting.web.os.web.annotation.Service;
import cn.donting.web.os.web.http.HttpMethod;
import cn.donting.web.os.web.http.HttpRequestMapping;
import cn.donting.web.os.web.http.HttpRequestMappingHandle;
import cn.donting.web.os.web.http.HttpRequestMappingParse;
import cn.donting.web.os.web.ioc.annotation.BeanImport;
import cn.donting.web.os.web.ioc.annotation.Import;
import cn.donting.web.os.web.log.AnsiOutput;
import cn.donting.web.os.web.log.LoggerConfig;
import cn.donting.web.os.web.util.PackageScanner;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
@Slf4j
public class SimpleWebIocApplication {

    private Map<String, Object> beans = new ConcurrentHashMap<>();
    private Map<HttpRequestMapping, HttpRequestMappingHandle> requestMappingHandleMap = new ConcurrentHashMap<>();

    public void run(Class<?> mainClass,String[] args) throws Exception {
        //开启彩色日志
        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);
        long l = System.currentTimeMillis();
        iocInit(mainClass);
        long end = System.currentTimeMillis();
        log.info("start 耗时："+(end-l));
    }
    private void iocInit(Class<?> mainClass) throws Exception {
        beans.put("SimpleWebIocApplication",this);
        List<Class> classes = PackageScanner.scanPackage(mainClass.getPackage().getName());
        List<Class> beanClasses = classes.stream().filter(c -> {
            if (c.getAnnotation(RestController.class) != null) {
                return true;
            }
            if (c.getAnnotation(Service.class) != null) {
                return true;
            }
            if (c.getAnnotation(Properties.class) != null) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());

        for (Class<?> aClass : beanClasses) {
            Object newInstance = aClass.newInstance();
            registerBeans(newInstance);
        }

        PropertiesLoader.loader(this);

        //RestController 可以延迟加载降低启动时间
        for (Object value : beans.values()) {
            RestController annotation = value.getClass().getAnnotation(RestController.class);
            if (annotation != null) {
                List<HttpRequestMappingHandle> handles = HttpRequestMappingParse.parseController(value);
                for (HttpRequestMappingHandle handle : handles) {
                    if (requestMappingHandleMap.containsKey(handle.getHttpRequest())) {
                        throw new RuntimeException(handle.getHttpRequest() + "重复");
                    }
                    requestMappingHandleMap.put(handle.getHttpRequest(), handle);
                }
            }
        }
        //自定义导入
        Import anImport = mainClass.getAnnotation(Import.class);
        if(anImport!=null){
            for (Class<? extends BeanImport> aClass : anImport.beanImport()) {
                BeanImport beanImport = aClass.newInstance();
                Map<String, Object> stringObjectMap = beanImport.importBeans(classes);
                Set<Map.Entry<String, Object>> entries = stringObjectMap.entrySet();
                for (Map.Entry<String, Object> entry : entries) {
                    registerBeans(entry.getKey(),entry.getValue());
                }
            }
        }
        //Autowired
        for (Object value : beans.values()) {
            Field[] declaredFields = value.getClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (declaredField.getAnnotation(Autowired.class) != null) {
                    Class type = declaredField.getType();
                    List<Object> objects = getBeanByType(type);

                    if (objects.size() > 1) {
                        throw new RuntimeException(type + " 找到" + objects.size() + "个");
                    }
                    declaredField.setAccessible(true);
                    declaredField.set(value, objects.get(0));
                }
            }
        }

        //ApplicationRunner
        for (Object value : beans.values()) {
            if (value instanceof ApplicationRunner) {
                ((ApplicationRunner) value).applicationRun();
            }
        }
    }




    public void doService(HttpServletRequest request, HttpServletResponse response) {
        String requestURI = request.getRequestURI();
        try {
            HttpRequestMapping httpRequestMapping = new HttpRequestMapping(HttpMethod.GET, requestURI);
            HttpRequestMappingHandle httpRequestMappingHandle = requestMappingHandleMap.get(httpRequestMapping);
            if (httpRequestMappingHandle != null) {
                httpRequestMappingHandle.doService(request, response);
            } else {
                response.setStatus(404);
                response.getOutputStream().write(("404 " + requestURI).getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                response.setStatus(500);
                response.getOutputStream().write(("error:" + requestURI).getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
            }
        }
    }

    public <T> List<T> getBeanByType(Class<T> aClass) {
        List<T> list = new ArrayList<>();
        for (Object value : beans.values()) {
            if (aClass.isAssignableFrom(value.getClass())) {
                list.add((T) value);
            }
        }
        if (list.size() == 0) {
            throw new RuntimeException(aClass + " 没有找到");
        }
        return list;
    }


    public List getAllBeans(){
        Collection<Object> values = beans.values();
        return new ArrayList(values);
    }

    public void registerBeans(Object object){
        registerBeans(object.getClass().getSimpleName(),object);
    }
    public void registerBeans(String beanName,Object object){
        if (beans.containsKey(beanName)) {
            throw new RuntimeException(beanName + " "+object.getClass().getName()+"重复");
        }
        beans.put(beanName, object);
    }
}
