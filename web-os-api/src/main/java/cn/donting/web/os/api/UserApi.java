package cn.donting.web.os.api;

import cn.donting.web.os.api.user.User;

public interface UserApi {
    /**
     * 当前登陆用户
     * @return null 未登录
     */
    User currentLoginUser();
}
