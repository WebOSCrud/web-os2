package cn.donting.web.os.core.service;

import cn.donting.web.os.api.user.User;
import cn.donting.web.os.core.domain.par.CreatUserParam;
import cn.donting.web.os.core.entity.OsUser;
import cn.donting.web.os.core.repository.OsUserRepository;
import cn.donting.web.os.core.service.api.FileSpaceApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Optional;

@Service
@Slf4j
public class UserService implements ApplicationRunner {

    @Autowired
    OsUserRepository osUserRepository;

    @Transactional
    public synchronized User creatUser(CreatUserParam creatUserParam) {
        Optional<OsUser> userOptional = osUserRepository.findById(creatUserParam.getUsername());
        if (userOptional.isPresent()) {
            throw new RuntimeException(creatUserParam.getUsername()+" 已经存在");
        }
        OsUser user = new OsUser();
        user.setUsername(creatUserParam.getUsername());
        user.setPassword(creatUserParam.getPassword());
        user.setDescription(creatUserParam.getDescription());
        user.setCreatTime(System.currentTimeMillis());
        log.info("creat user:{}",creatUserParam.getUsername());
        osUserRepository.save(user);
        user.setPassword(null);
        //创建user 用户文件夹
        File userDir = new File(FileSpaceApi.usersDir, creatUserParam.getUsername());
        if (!userDir.mkdirs()) {
            throw new RuntimeException(userDir+" 创建失败请检查特殊字符");
        }
        userDir = new File(userDir, "desktop");
        userDir.mkdirs();
        return user;
    }

    @Override
    public void run(ApplicationArguments args)  {
        Optional<OsUser> root = osUserRepository.findById("root");
        if(root.isPresent()){
            return;
        }
        CreatUserParam param = new CreatUserParam();
        param.setUsername("root");
        param.setDescription("root");
        param.setPassword("root");
        creatUser(param);
    }
}
