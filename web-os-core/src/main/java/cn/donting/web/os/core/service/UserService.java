package cn.donting.web.os.core.service;

import cn.donting.web.os.core.db.entity.OsUser;
import cn.donting.web.os.core.db.mapper.UserMapper;
import cn.donting.web.os.web.annotation.Autowired;
import cn.donting.web.os.web.annotation.Service;
import cn.donting.web.os.web.ioc.ApplicationRunner;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService implements ApplicationRunner {
    @Autowired
    UserMapper userMapper;
    @Override
    public void applicationRun() throws Exception {

        OsUser root = userMapper.findById("root");
        if(root==null){
            root=new OsUser();
            root.setUsername("root");
            root.setCreatTime(System.currentTimeMillis());
            root.setPassword("root");
            root.setDescription("root 用户");
            log.info("创建root 用户");
            userMapper.save(root);
        }

    }
}
