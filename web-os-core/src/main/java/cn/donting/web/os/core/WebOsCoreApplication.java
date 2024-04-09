package cn.donting.web.os.core;

import cn.donting.web.os.StartImpl;
import cn.donting.web.os.web.ioc.SimpleWebIocApplication;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WebOsCoreApplication implements StartImpl {
   private SimpleWebIocApplication simpleIocApplication;

    @Override
    public void startSpring(ServletContext servletContext) throws Exception {
        simpleIocApplication = new SimpleWebIocApplication();
        simpleIocApplication.run(WebOsCoreApplication.class);
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        simpleIocApplication.doService(request,response);
    }
}
