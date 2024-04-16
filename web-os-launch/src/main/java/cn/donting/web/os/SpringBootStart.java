package cn.donting.web.os;

import javax.servlet.ServletContext;

public interface SpringBootStart {
    void start(Class mainClass,ServletContext servletContext ,String[] args) throws Exception;
}
