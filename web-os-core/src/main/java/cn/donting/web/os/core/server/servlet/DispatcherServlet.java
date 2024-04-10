package cn.donting.web.os.core.server.servlet;

import cn.donting.web.os.web.annotation.Autowired;
import cn.donting.web.os.web.annotation.Service;
import cn.donting.web.os.web.ioc.ApplicationRunner;
import cn.donting.web.os.web.ioc.SimpleWebIocApplication;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class DispatcherServlet extends HttpServlet implements ApplicationRunner {

    @Autowired
    SimpleWebIocApplication simpleWebIocApplication;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        simpleWebIocApplication.doService(req, resp);
    }

    @Override
    public void applicationRun() throws Exception {

    }
}
