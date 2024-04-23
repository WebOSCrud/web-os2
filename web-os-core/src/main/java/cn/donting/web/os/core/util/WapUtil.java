package cn.donting.web.os.core.util;

import cn.donting.web.os.core.wap.loader.WapClassLoader;
import cn.hutool.core.io.FileUtil;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class WapUtil {

    public static URL getWapResources(WapClassLoader wapClassLoader, String name) {
        URL resource = wapClassLoader.getResource(name);
        return resource;
    }

    public static ResourceInfo getWapResourceInfo(WapClassLoader wapClassLoader, String name) {
        URL resource = wapClassLoader.getResource(name);
        String resourceName = FileUtil.getName(resource.getPath());
        String extName = FileUtil.extName(resourceName);
        return new ResourceInfo(resource,resourceName,extName);
    }

    @Getter
    public static class ResourceInfo {
        private final URL url;
        private final String name;
        private final String extName;

        public ResourceInfo(URL url, String name, String extName) {
            this.url = url;
            this.name = name;
            this.extName = extName;
        }

        public void saveFile(File file){
            try(InputStream inputStream = url.openStream()){
                FileUtil.writeFromStream(inputStream,file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
