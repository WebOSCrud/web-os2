package cn.donting.web.os.core.service;

import cn.donting.web.os.api.user.User;
import cn.donting.web.os.core.domain.par.CreatUserParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService implements ApplicationRunner {


    public synchronized User creatUser(CreatUserParam creatUserParam) {

        return null;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

    }
}
