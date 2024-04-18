package cn.donting.web.os.core;

import cn.donting.web.os.OsStart;
import cn.donting.web.os.SpringBootStart;
import cn.donting.web.os.core.server.JettyServletContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@SpringBootApplication
public class WebOsCoreApplication extends SpringBootServletInitializer implements OsStart {

    public static final String OS_ID="os";

    private String[] args;
    public static void main(String[] args) throws Exception {
        SpringApplication.run(WebOsCoreApplication.class);
    }
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        builder.sources(WebOsCoreApplication.class);
        return super.configure(builder);
    }

    @Override
    protected WebApplicationContext run(SpringApplication application) {
        return (WebApplicationContext) application.run(args);
    }

    @Override
    public void start(String[] args) throws ServletException {
        this.args=args;
        onStartup(new JettyServletContext());
    }



}
