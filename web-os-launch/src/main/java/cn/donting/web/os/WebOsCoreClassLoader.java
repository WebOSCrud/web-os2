package cn.donting.web.os;

import java.net.URL;
import java.net.URLClassLoader;

public class WebOsCoreClassLoader extends URLClassLoader {
    public WebOsCoreClassLoader(URL[] urls) {
        super(urls);
    }
}
