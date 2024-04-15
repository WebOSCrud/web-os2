package cn.donting.web.os.core.server;

import cn.donting.web.os.core.properties.ServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
@Slf4j
public class WebOsJettyServer implements ApplicationRunner {


    @Override
    public void run(ApplicationArguments args) throws Exception {
        long l = System.currentTimeMillis();
        ServerProperties serverProperties = new ServerProperties();
        Server server = new Server(serverProperties.getPort());
        ServletContextHandler contextHandler = new ServletContextHandler(server, "/", false, false);
        ServletHolder servletHolder = new ServletHolder(new Disp());
        contextHandler.addServlet(servletHolder, "/");
        server.setHandler(contextHandler);
        server.start();
        long end = System.currentTimeMillis();
        log.info("jetty start:[{}]  {}ms", serverProperties.getPort(), end - l);
    }

    public  class Disp implements Servlet {



        @Override
        public void init(ServletConfig config) throws ServletException {
//            dispatcherServlet.init(config);
        }

        @Override
        public ServletConfig getServletConfig() {
//            return dispatcherServlet.getServletConfig();
            return null;
        }

        @Override
        public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
            try {
//                dispatcherServlet.service(req, res);
            }catch (Exception ex){
                ex.printStackTrace();
                ((HttpServletResponse)res).setStatus(404);
                req.setAttribute(RequestDispatcher.ERROR_MESSAGE,ex.getMessage());
                req.setAttribute(RequestDispatcher.ERROR_STATUS_CODE,500);
                req.getRequestDispatcher("/error").forward(req,res);

            }
        }

        @Override
        public String getServletInfo() {
//            return dispatcherServlet.getServletInfo();
            return "";
        }

        @Override
        public void destroy() {
//            dispatcherServlet.destroy();
        }
    }
}
