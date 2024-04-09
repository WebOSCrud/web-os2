package cn.donting.web.os.web.http;

import cn.donting.web.os.web.annotation.RequestBody;
import cn.donting.web.os.web.annotation.RequestParam;
import cn.donting.web.os.web.http.par.BodyParHandle;
import cn.donting.web.os.web.http.par.QueryParHandle;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestMappingHandle {
    private final Method declaredMethod;
    private final Object target;
    private final ParHandel[] parHandels;

    private final HttpRequestMapping httpRequest;

    private ObjectMapper objectMapper = new ObjectMapper();


    public HttpRequestMappingHandle(HttpRequestMapping httpRequest, Method declaredMethod, Object target) {
        this.declaredMethod = declaredMethod;
        List<ParHandel> parHandels = new ArrayList<>();
        for (Parameter parameter : declaredMethod.getParameters()) {
            RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
            if (requestParam != null) {
                QueryParHandle queryParHandle = new QueryParHandle(requestParam, parameter.getType());
                parHandels.add(queryParHandle);
            }
            RequestBody requestBody = parameter.getAnnotation(RequestBody.class);
            if (requestBody != null) {
                BodyParHandle queryParHandle = new BodyParHandle(parameter.getType());
                parHandels.add(queryParHandle);
            }
        }
        this.parHandels = parHandels.toArray(new ParHandel[parHandels.size()]);
        this.httpRequest = httpRequest;
        this.target = target;
    }

    public interface ParHandel {
        Object handle(Map<String, String> query, HttpServletRequest request, HttpServletResponse response);
    }

    public HttpRequestMapping getHttpRequest() {
        return httpRequest;
    }

    public void doService(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        HashMap<String, String> query = new HashMap<>();
        String queryString = httpServletRequest.getQueryString();
        if (queryString != null) {
            String[] keyValues = queryString.split("&");
            for (String keyValue : keyValues) {
                String[] split = keyValue.split("=");
                query.put(split[0], split[1]);
            }
        }
        Object[] par = new Object[parHandels.length];
        for (int i = 0; i < parHandels.length; i++) {
            ParHandel parHandel = parHandels[i];
            Object parValue = parHandel.handle(query, httpServletRequest, httpServletResponse);
            par[i] = parValue;
        }
        Object result = declaredMethod.invoke(target, par);
        if (isPrimitive(result)) {
            httpServletResponse.setContentType("text/plain");
            httpServletResponse.getOutputStream().write(((String) result).getBytes(StandardCharsets.UTF_8));
            return;
        } else {
            String valueAsString = objectMapper.writeValueAsString(result);
            httpServletResponse.setContentType("application/json");
            httpServletResponse.getOutputStream().write(valueAsString.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static boolean isPrimitive(Object obj) {
        return obj instanceof Boolean ||
                obj instanceof Byte ||
                obj instanceof Character ||
                obj instanceof Short ||
                obj instanceof Integer ||
                obj instanceof Long ||
                obj instanceof Float ||
                obj instanceof Double ||
                obj instanceof String;
    }
}
