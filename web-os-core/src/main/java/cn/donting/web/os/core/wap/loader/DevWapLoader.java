package cn.donting.web.os.core.wap.loader;

import cn.donting.web.os.core.wap.WapLoader;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class DevWapLoader extends WapLoader {
    public DevWapLoader(File file) {
        super(file);
    }



    @Override
    protected WapClassLoader loadClassLoader() {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            List<URL> urls = new ArrayList<>();
            for (int i = 1; i < lines.size(); i++) {
                File file = new File(lines.get(i));
                urls.add(file.toURI().toURL());
            }
            WapClassLoader classLoader = new WapClassLoader(urls.toArray(new URL[urls.size()]));
            return classLoader;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
}
