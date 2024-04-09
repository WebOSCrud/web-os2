package cn.donting.web.os.web.http.par;

import cn.donting.web.os.web.http.HttpRequestMappingHandle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class BodyParHandle implements HttpRequestMappingHandle.ParHandel {

    private final Class<?> paramType;

    public BodyParHandle(Class<?> type) {
        this.paramType=type;
    }

    @Override
    public Object handle(Map<String, String> query, HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
}
