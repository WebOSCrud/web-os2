package cn.donting.web.os.web.http;

import cn.donting.web.os.web.annotation.DeleteMapping;
import cn.donting.web.os.web.annotation.GetMapping;
import cn.donting.web.os.web.annotation.PostMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class HttpRequestMappingParse {

    public static List<HttpRequestMappingHandle> parseController(Object o){
        List<HttpRequestMappingHandle> handles=new ArrayList<>();
        for (Method declaredMethod : o.getClass().getDeclaredMethods()) {
            handles.addAll(parseControllerMethod(declaredMethod, o));
        }
        return handles;
    }

    private static List<HttpRequestMappingHandle> parseControllerMethod(Method declaredMethod, Object o) {
        GetMapping getMapping = declaredMethod.getAnnotation(GetMapping.class);
        PostMapping postMapping = declaredMethod.getAnnotation(PostMapping.class);
        DeleteMapping deleteMapping = declaredMethod.getAnnotation(DeleteMapping.class);
        List<HttpRequestMappingHandle> handles=new ArrayList<>();
        if(getMapping!=null){
            String url=getMapping.value();
            HttpRequestMappingHandle requestParameter = new HttpRequestMappingHandle(
                    new HttpRequestMapping(HttpMethod.GET,url)
                    ,declaredMethod, o);
            handles.add(requestParameter);
        }
        if(postMapping!=null){
            String url=postMapping.value();
            HttpRequestMappingHandle requestParameter = new HttpRequestMappingHandle(
                    new HttpRequestMapping(HttpMethod.POST,url)
                    ,declaredMethod, o);
            handles.add(requestParameter);
        }
        if(deleteMapping!=null){
            String url=deleteMapping.value();
            HttpRequestMappingHandle requestParameter = new HttpRequestMappingHandle(
                    new HttpRequestMapping(HttpMethod.DELETE,url)
                    ,declaredMethod, o);
            handles.add(requestParameter);
        }
        return handles;
    }
}
