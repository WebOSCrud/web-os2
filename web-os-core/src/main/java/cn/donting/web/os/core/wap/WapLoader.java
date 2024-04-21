package cn.donting.web.os.core.wap;

import cn.donting.web.os.api.wap.WapInfo;
import cn.donting.web.os.core.wap.loader.DevWapLoader;
import cn.donting.web.os.core.wap.loader.WapClassLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;
@Slf4j
public abstract class WapLoader {

    protected final File file;
    private final WapClassLoader wapClassLoader;

    public WapLoader(File file) {
        this.file = file;
        wapClassLoader = loadClassLoader();
    }

    protected abstract WapClassLoader loadClassLoader();

    public WapClassLoader getWapClassLoader(){
        return wapClassLoader;
    }

    public static WapLoader getWapLoader(File file) {
        log.info("load WapLoader:{}",file);
        if (file.getName().endsWith(".wev")) {
            DevWapLoader devWapLoader = new DevWapLoader(file);
            return devWapLoader;
        }
        throw new RuntimeException(file+"无法加载");

    }
}
