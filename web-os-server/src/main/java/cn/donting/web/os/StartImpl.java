package cn.donting.web.os;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface StartImpl {

    void startSpring(ServletContext servletContext) throws Exception;

    void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
