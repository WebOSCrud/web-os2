package cn.donting.web.os.launch;

import cn.donting.web.os.OsStart;
import cn.donting.web.os.SpringBootStart;
import cn.donting.web.os.WebOsCoreClassLoader;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        List<String> agrsList=new ArrayList<>();
        agrsList.addAll(Arrays.stream(args).collect(Collectors.toList()));
        // 调用main方法，传入当前类的main方法需要的参数
        springBootStart.start(agrsList.toArray(new String[agrsList.size()]));
    }

    protected abstract List<URL> getClasspathURL(File file);
    protected abstract String getMainClass(File file);
}
