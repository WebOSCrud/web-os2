package cn.donting.web.os.core.server;

import org.eclipse.jetty.server.handler.ErrorHandler;

import javax.servlet.http.HttpServletRequest;

public class OsErrorHandler extends ErrorHandler implements ErrorHandler.ErrorPageMapper {
    @Override
    public String getErrorPage(HttpServletRequest request) {
        return "/os/error";
    }
}
