package cn.donting.web.os.core.service;

import cn.donting.web.os.api.WapApi;
import cn.donting.web.os.core.service.api.FileSpaceApi;
import cn.donting.web.os.core.wap.WapLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class WapService implements ApplicationRunner {

    @Autowired
    WapApi wapApi;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        File installDir = FileSpaceApi.installDir;
        File[] files = installDir.listFiles();
        for (File file : files) {
            wapApi.install(file);
        }
    }
}
