package cn.donting.web.os.web.ioc;

import cn.donting.web.os.web.annotation.Autowired;
import cn.donting.web.os.web.annotation.RestController;
import cn.donting.web.os.web.annotation.Service;
import cn.donting.web.os.web.http.HttpMethod;
import cn.donting.web.os.web.http.HttpRequestMapping;
import cn.donting.web.os.web.http.HttpRequestMappingHandle;
import cn.donting.web.os.web.http.HttpRequestMappingParse;
import cn.donting.web.os.web.util.PackageScanner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SimpleWebIocApplication {
    private static final Logger logger = Logger.getLogger(SimpleWebIocApplication.class.getName());

    private Map<String, Object> beans = new ConcurrentHashMap<>();
    private Map<HttpRequestMapping, HttpRequestMappingHandle> requestMappingHandleMap = new ConcurrentHashMap<>();

    public void run(Class<?> mainClass) throws Exception {
        long l = System.currentTimeMillis();
        iocInit(mainClass);
        long end = System.currentTimeMillis();
        logger.log(Level.INFO,"start 耗时："+(end-l));
    }
    private void iocInit(Class<?> mainClass) throws Exception {
        beans.put("SimpleWebIocApplication",this);
        List<Class<?>> classes = PackageScanner.scanPackage(mainClass.getPackage().getName());
        classes = classes.stream().filter(c -> {
            if (c.getAnnotation(RestController.class) != null) {
                return true;
            }
            if (c.getAnnotation(Service.class) != null) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());

        for (Class<?> aClass : classes) {
            Object newInstance = aClass.newInstance();
            if (beans.containsKey(aClass.getSimpleName())) {
                throw new RemoteException(aClass.getSimpleName() + " 重复");
            }
            beans.put(aClass.getSimpleName(), newInstance);
        }
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
}
