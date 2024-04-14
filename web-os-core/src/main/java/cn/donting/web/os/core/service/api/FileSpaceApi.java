package cn.donting.web.os.core.service.api;

import cn.donting.web.os.web.annotation.Service;
import cn.hutool.core.io.FileUtil;

import java.io.File;
@Service
public class FileSpaceApi implements cn.donting.web.os.api.FileSpaceApi {

    public static final File dataDir = new File("./.web-os-data");
    public static final File dbDir = new File(dataDir,"db");
    public static final File wapDir = new File(dataDir,"wap");
    public static final File usersDir = new File(dataDir,"users");
    static {
        FileUtil.mkdir(dataDir);
        FileUtil.mkdir(dbDir);
        FileUtil.mkdir(wapDir);
        FileUtil.mkdir(usersDir);
    }

    @Override
    public File loginUserSpace() {
        return null;
    }

    @Override
    public File wapSpace(String wapId) {
        return null;
    }
}
