package cn.donting.web.os.core.server;

import cn.donting.web.os.core.properties.ServerProperties;
import cn.donting.web.os.core.server.servlet.DispatcherServlet;
import cn.donting.web.os.web.annotation.Autowired;
import cn.donting.web.os.web.annotation.Service;
import cn.donting.web.os.web.ioc.ApplicationRunner;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

@Service
@Slf4j
public class WebOsJettyServer implements ApplicationRunner {

    @Autowired
    DispatcherServlet dispatcherServlet;

    @Override
    public void applicationRun() {
        try {
            long l = System.currentTimeMillis();
            ServerProperties serverProperties = new ServerProperties();
            Server server = new Server(serverProperties.getPort());
            ServletContextHandler contextHandler = new ServletContextHandler(server, "/", false, false);
            ServletHolder servletHolder = new ServletHolder(dispatcherServlet);
            contextHandler.addServlet(servletHolder, "/");
            server.setHandler(contextHandler);
            server.start();
            long end = System.currentTimeMillis();
//            log("jetty start:[" + serverProperties.getPort() + "]  "+(end-l)+"ms");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
}
