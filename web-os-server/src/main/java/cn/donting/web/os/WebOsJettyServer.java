package cn.donting.web.os;

import cn.donting.web.os.handler.DispatcherServletServetHandler;
import cn.donting.web.os.properties.ServerProperties;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.*;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;


public class WebOsJettyServer {
    private static StartImpl startImpl;
    private static URLClassLoader urlClassLoader;
    private static WebAppContext webAppContext;
    private static  List<Filter> filter ;
    private static final Logger logger = Log.getLogger(WebOsJettyServer.class);

    private Handler applyWrapper(Handler handler, HandlerWrapper wrapper) {
        wrapper.setHandler(handler);
        return wrapper;
    }

    public static void main(String[] args) throws Exception {
        ServerProperties serverProperties = new ServerProperties();


        Server server = new Server(serverProperties.getPort());

        ServletContextHandler contextHandler = new ServletContextHandler(server, "/", false, false);
        ServletHolder servletHolder = new ServletHolder(new HelloServlet());
        contextHandler.addServlet(servletHolder, "/");
        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.FORWARD,
                DispatcherType.ERROR,
                DispatcherType.REQUEST,
                DispatcherType.INCLUDE,
                DispatcherType.ASYNC);
        dispatcherTypes.addAll(Arrays.stream(DispatcherType.values()).collect(Collectors.toList()));


        server.setHandler(contextHandler);

        server.start();
        List<String> urls = Files.readAllLines(new File("./web-os-core.wev").toPath());
        urls.remove(0);
        URL[] array = (URL[]) urls.stream().map(u -> {
            try {
                return new File(u).toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()).toArray(new URL[1]);

        urlClassLoader = new URLClassLoader(array);
        Class<?> aClass = urlClassLoader.loadClass("cn.donting.web.os.core.WebOsCoreApplication");
        startImpl = (StartImpl) aClass.newInstance();

        Thread.currentThread().setContextClassLoader(urlClassLoader);

        startImpl.startSpring(new MyServletContext());

        logger.info("jetty start:[" + serverProperties.getPort() + "]");

        server.join();
    }

    // 定义一个简单的Servlet类
    static class HelloServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
                Thread.currentThread().setContextClassLoader(urlClassLoader);
                startImpl.service(req, resp);
        }
    }

    static class Sel extends AbstractHandler {


        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            Thread.currentThread().setContextClassLoader(urlClassLoader);
            webAppContext.doScope(target, baseRequest, request, response);
            startImpl.service(request, response);
        }
    }
}
