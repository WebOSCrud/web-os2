package cn.donting.web.os.launch;

import cn.donting.web.os.OsStart;
import cn.donting.web.os.SpringBootStart;
import cn.donting.web.os.WebOsCoreClassLoader;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;

public abstract class WebOsCoreLaunch {

    protected final File file;
    public WebOsCoreLaunch(File file) {
        this.file = file;
    }

    public void launch(String[] args) throws Exception {
        List<URL> classpathURL = getClasspathURL(file);
        WebOsCoreClassLoader webOsCoreClassLoader = new WebOsCoreClassLoader(classpathURL.toArray(new URL[classpathURL.size()]));
        Class<?> aClass = webOsCoreClassLoader.loadClass(getMainClass(file));
        OsStart springBootStart = (OsStart)aClass.newInstance();
        Thread.currentThread().setContextClassLoader(webOsCoreClassLoader);
        // 调用main方法，传入当前类的main方法需要的参数
        springBootStart.start(new String[]{"--spring.output.ansi.enabled=always"});
    }

    protected abstract List<URL> getClasspathURL(File file);
    protected abstract String getMainClass(File file);
}
