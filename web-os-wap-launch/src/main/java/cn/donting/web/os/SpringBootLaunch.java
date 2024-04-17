package cn.donting.web.os;


import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import javax.servlet.ServletContext;

public class SpringBootLaunch extends SpringBootServletInitializer implements SpringBootStart{
    private Class mainClass;
    private String[] args;
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        builder.sources(mainClass);
        return super.configure(builder);
    }

    @Override
    protected SpringApplicationBuilder createSpringApplicationBuilder() {
        return new ArgsSpringApplicationBuilder(args);
    }

    @Override
    public void start(Class mainClass, ServletContext servletContext, String[] args) throws Exception {
        this.mainClass=mainClass;
        this.args=args;
        onStartup(servletContext);
    }


}
