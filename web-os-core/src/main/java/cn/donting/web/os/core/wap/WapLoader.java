package cn.donting.web.os.core.wap;

import cn.donting.web.os.api.wap.WapInfo;
import cn.donting.web.os.core.wap.loader.DevWapLoader;
import cn.donting.web.os.core.wap.loader.WapClassLoader;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public abstract class WapLoader {

    protected final File file;
    private final WapClassLoader wapClassLoader;
    private final WapInfo wapInfo;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public WapLoader(File file) {
        this.file = file;
        wapClassLoader = loadClassLoader();
        wapInfo = loader();
    }

    public WapInfo loader() {
        try {
            URL resource = wapClassLoader.getResource("wap.json");
            return objectMapper.readValue(resource, WapInfo.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    protected abstract WapClassLoader loadClassLoader();

    public Wap getWap() {

        return null;
    }

    public WapInfo getWapInfo() {
        return wapInfo;
    }


    public WapClassLoader getWapClassLoader(){
        return wapClassLoader;
    }
    public static WapLoader getWapLoader(File file) {
        if (file.getName().endsWith(".wev")) {
            DevWapLoader devWapLoader = new DevWapLoader(file);
            return devWapLoader;
        }
        throw new RuntimeException(file+"无法加载");

    }
}
