package cn.donting.web.os.core.server;

import cn.donting.web.os.core.properties.ServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.ServletRequestPathUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletMapping;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
@Slf4j
public class WebOsJettyServer implements ApplicationRunner {

    @Autowired
    DispatcherServlet dispatcherServlet;

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
            dispatcherServlet.init(config);
        }

        @Override
        public ServletConfig getServletConfig() {
            return dispatcherServlet.getServletConfig();
        }

        /**
         *
         * {@link ServletRequestPathUtils#parseAndCache}
         *
         * @param req the <code>ServletRequest</code> object that contains the client's request
         * @param res the <code>ServletResponse</code> object that contains the servlet's response
         * @see HttpServletMapping / 首页转发
         *
         * @throws ServletException
         * @throws IOException
         */
        @Override
        public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
            try {
                Request request=(Request)req;
                //用于指定 request 的 ContextPath。
                request.setContextPath("/app");
                dispatcherServlet.service(req, res);
            }catch (Exception ex){
                ex.printStackTrace();
                ((HttpServletResponse)res).setStatus(404);
                req.setAttribute(RequestDispatcher.ERROR_MESSAGE,ex.getMessage());
                req.setAttribute(RequestDispatcher.ERROR_STATUS_CODE,500);
                req.getRequestDispatcher("/app/error").forward(req,res);

            }
        }

        @Override
        public String getServletInfo() {
            return dispatcherServlet.getServletInfo();
//            return "";
        }

        @Override
        public void destroy() {
            dispatcherServlet.destroy();
        }
    }
}
