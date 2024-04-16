package cn.donting.web.os.core.service.api;

import cn.donting.web.os.api.wap.WapInstallInfo;

import java.io.File;
import java.util.List;

public class WapApi implements cn.donting.web.os.api.WapApi {
    @Override
    public WapInstallInfo install(File file) {
        return null;
    }

    @Override
    public void uninstall(String wapId) {

    }

    @Override
    public List<WapInstallInfo> getInstallList() {
        return null;
    }
}
