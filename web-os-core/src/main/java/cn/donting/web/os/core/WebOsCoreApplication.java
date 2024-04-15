package cn.donting.web.os.core;

import cn.donting.web.os.SpringBootStart;
import cn.donting.web.os.core.server.JettyServletContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import javax.servlet.ServletException;

@SpringBootApplication
public class WebOsCoreApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(WebOsCoreApplication.class);
    }

    public static class Tun extends SpringBootServletInitializer implements SpringBootStart {
        @Override
        protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
            builder.sources(WebOsCoreApplication.class);
            return super.configure(builder);
        }

        @Override
        public void start() throws ServletException {
            JettyServletContext jettyServletContext = new JettyServletContext();
            onStartup(jettyServletContext);
        }
    }

}
