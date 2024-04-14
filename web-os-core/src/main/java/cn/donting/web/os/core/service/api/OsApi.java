package cn.donting.web.os.core.service.api;

import cn.donting.web.os.WebOsLaunch;
import cn.donting.web.os.api.FileSpaceApi;
import cn.donting.web.os.api.UserApi;
import cn.donting.web.os.api.WapApi;
import cn.donting.web.os.api.WebOsApi;
import cn.donting.web.os.web.annotation.Service;

@Service
public class OsApi implements WebOsApi {

    public OsApi() {
        WebOsLaunch.setWebOsApi(this);
    }

    @Override
    public UserApi userApi() {
        return null;
    }

    @Override
    public FileSpaceApi fileSpaceApi() {
        return null;
    }

    @Override
    public WapApi wapApi() {
        return null;
    }
}
