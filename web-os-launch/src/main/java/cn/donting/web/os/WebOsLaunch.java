package cn.donting.web.os;


import cn.donting.web.os.api.WebOsApi;
import cn.donting.web.os.launch.DevWebOsCoreLaunch;
import cn.donting.web.os.launch.WebOsCoreLaunch;

import java.io.File;

public class WebOsLaunch {

    private static WebOsApi webOsApi;

    public static void main(String[] args) throws Exception {
        File file = new File("./web-os-core.wev");
        WebOsCoreLaunch webOsCoreLaunch;
        if (file.exists()) {
            webOsCoreLaunch = new DevWebOsCoreLaunch(file);
        }else {
            throw new RuntimeException("暂不支持");
        }
        webOsCoreLaunch.launch(args);
    }

    public static void setWebOsApi(WebOsApi webOsApi) {
        if(WebOsLaunch.webOsApi!=null){
            return;
        }
        WebOsLaunch.webOsApi = webOsApi;
    }
}
