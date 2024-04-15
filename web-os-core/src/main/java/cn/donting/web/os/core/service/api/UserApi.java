package cn.donting.web.os.core.service.api;

import cn.donting.web.os.api.user.User;
import org.springframework.stereotype.Service;

@Service
public class UserApi implements cn.donting.web.os.api.UserApi {

    @Override
    public User currentLoginUser() {
        return null;
    }
}
