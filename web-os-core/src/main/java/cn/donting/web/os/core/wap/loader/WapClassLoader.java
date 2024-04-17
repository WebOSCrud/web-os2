package cn.donting.web.os.core.wap.loader;


import java.net.URL;
import java.net.URLClassLoader;

public class WapClassLoader extends URLClassLoader {
    public WapClassLoader(URL[] urls) {
        super(urls);
    }
}
