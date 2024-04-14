package cn.donting.web.os.api;

import java.io.File;

public interface FileSpaceApi {

    /**
     * 获取登陆用户的用户空间
     *
     * @return
     */
    File loginUserSpace();

    /**
     * 获取一个wap的应用空间
     *
     * @param wapId
     * @return
     */
    File wapSpace(String wapId);


}
