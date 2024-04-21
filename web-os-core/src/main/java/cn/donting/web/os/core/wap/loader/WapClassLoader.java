package cn.donting.web.os.core.wap.loader;


import cn.donting.web.os.api.wap.WapInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public class WapClassLoader extends URLClassLoader {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private WapInfo wapInfo;

    public WapClassLoader(URL[] urls) {
        super(urls);
        try {
            URL resource = getResource("wap.json");
            wapInfo = objectMapper.readValue(resource, WapInfo.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String getWapId() {
        return wapInfo.getId();
    }

    public WapInfo getWapInfo() {
        return wapInfo;
    }
}
