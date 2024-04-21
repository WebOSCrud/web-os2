package cn.donting.web.os.core.service.api;

import cn.donting.web.os.api.UserApi;
import cn.hutool.core.io.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
@Service
public class FileSpaceApi implements cn.donting.web.os.api.FileSpaceApi {

    /**
     * data 数据根目录
     */
    public static final File dataDir = new File("./.web-os-data");
    /**
     * 数据库目录
     */
    public static final File dbDir = new File(dataDir,"db");
    /**
     * wap 安装后的目录
     */
    public static final File wapDir = new File(dataDir,"wap");
    /**
     * 用户空间目录
     */
    public static final File usersDir = new File(dataDir,"users");
    /**
     * 自动安装目录
     */
    public static final File installDir = new File(dataDir,"install");
    /**
     * 静态资源目录
     */
    public static final File staticDir = new File(dataDir,"static");
    /**
     * 文件类型 图标文件目录
     */
    public static final File fileTypeIconDir = new File(staticDir,"fileType");
    static {
        FileUtil.mkdir(dataDir);
        FileUtil.mkdir(dbDir);
        FileUtil.mkdir(wapDir);
        FileUtil.mkdir(usersDir);
        FileUtil.mkdir(installDir);
        FileUtil.mkdir(staticDir);
        FileUtil.mkdir(fileTypeIconDir);
    }

    @Autowired
    UserApi userApi;

    @Override
    public File loginUserSpace() {
        return new File(usersDir,userApi.currentLoginUser().toString());
    }

    @Override
    public File wapSpace(String wapId) {
        return new File(wapDir,wapId);
    }
}
