package cn.donting.web.os.core.service.api;

import cn.donting.web.os.WebOsLaunch;
import cn.donting.web.os.api.FileSpaceApi;
import cn.donting.web.os.api.UserApi;
import cn.donting.web.os.api.WapApi;
import cn.donting.web.os.api.WebOsApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OsApi implements WebOsApi {
    @Autowired
    UserApi userApi;

    @Autowired
    FileSpaceApi fileSpaceApi;

    @Autowired
    WapApi wapApi;
    public OsApi() {
        WebOsLaunch.setWebOsApi(this);
    }

    @Override
    public UserApi userApi() {
        return userApi;
    }

    @Override
    public FileSpaceApi fileSpaceApi() {
        return fileSpaceApi;
    }

    @Override
    public WapApi wapApi() {
        return wapApi;
    }
}
