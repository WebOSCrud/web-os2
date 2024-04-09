package cn.donting.web.os.web.http.par;

import cn.donting.web.os.web.annotation.RequestParam;
import cn.donting.web.os.web.http.HttpRequestMappingHandle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class QueryParHandle implements HttpRequestMappingHandle.ParHandel {

    private final RequestParam requestParam;
    private final Class<?> paramType;

    public QueryParHandle(RequestParam requestParam, Class<?> paramType) {
        this.requestParam = requestParam;
        this.paramType = paramType;
    }

    @Override
    public Object handle(Map<String, String> query, HttpServletRequest request, HttpServletResponse response) {
        String value = query.get(requestParam.value());
        if(paramType.equals(int.class)){
            if (value == null) {
                return 0;
            }else {
                return Integer.parseInt(value);
            }
        }
        return null;
    }
}
