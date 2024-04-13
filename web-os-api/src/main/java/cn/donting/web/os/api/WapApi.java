package cn.donting.web.os.api;

import cn.donting.web.os.api.wap.WapInstallInfo;

import java.io.File;
import java.util.List;

/**
 * wap 相关操作的API
 */
public interface WapApi {
    /**
     * 从文件安装Wap
     * @param file
     * @return
     */
    WapInstallInfo install(File file);

    /**
     * 卸载WAP
     * @param wapId
     */
    void uninstall(String wapId);

    /**
     * 获取安装列表
     * @return
     */
    List<WapInstallInfo> getInstallList();


}
