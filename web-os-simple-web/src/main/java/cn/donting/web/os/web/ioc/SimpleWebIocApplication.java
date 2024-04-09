package cn.donting.web.os.web.ioc;

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
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SimpleWebIocApplication {

    private Map<String, Object> beans = new ConcurrentHashMap<>();

    private Map<HttpRequestMapping, HttpRequestMappingHandle> requestMappingHandleMap = new ConcurrentHashMap<>();

    public void run(Class<?> mainClass) throws Exception {
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
        for (Object value : beans.values()) {
            RestController annotation = value.getClass().getAnnotation(RestController.class);
            if (annotation != null) {
                List<HttpRequestMappingHandle> handles = HttpRequestMappingParse.parseController(value);
                for (HttpRequestMappingHandle handle : handles) {
                    if (requestMappingHandleMap.containsKey(handle.getHttpRequest())) {
                        throw new RuntimeException(handle.getHttpRequest() + "重复");
                    }
                    requestMappingHandleMap.put(handle.getHttpRequest(),handle);
                }
            }
        }
    }


    public void doService(HttpServletRequest request, HttpServletResponse response){
        String requestURI = request.getRequestURI();
        try {
            HttpRequestMapping httpRequestMapping = new HttpRequestMapping(HttpMethod.GET, requestURI);
            HttpRequestMappingHandle httpRequestMappingHandle = requestMappingHandleMap.get(httpRequestMapping);
            if (httpRequestMappingHandle != null) {
                httpRequestMappingHandle.doService(request, response);
            }else {
                response.setStatus(404);
                response.getOutputStream().write(("404 "+requestURI).getBytes(StandardCharsets.UTF_8));
            }
        }catch (Exception ex){
            ex.printStackTrace();
            try {
                response.setStatus(500);
                response.getOutputStream().write(("error:"+requestURI).getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
            }
        }
    }
}
