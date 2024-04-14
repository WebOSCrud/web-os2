package cn.donting.web.os.core.service;

import cn.donting.web.os.api.user.User;
import cn.donting.web.os.core.db.DbManger;
import cn.donting.web.os.core.db.entity.OsUser;
import cn.donting.web.os.core.db.mapper.UserMapper;
import cn.donting.web.os.core.domain.par.CreatUserParam;
import cn.donting.web.os.web.annotation.Autowired;
import cn.donting.web.os.web.annotation.Service;
import cn.donting.web.os.web.ioc.ApplicationRunner;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;

@Service
@Slf4j
public class UserService implements ApplicationRunner {
    @Autowired
    UserMapper userMapper;


    public synchronized User creatUser(CreatUserParam creatUserParam) {
        DbManger.beginTransaction();
        OsUser user = userMapper.findById(creatUserParam.getUsername());
        if(user!=null){
            throw new RuntimeException(creatUserParam.getUsername()+"已经存在");
        }
        user=new OsUser();
        user.setDescription(creatUserParam.getDescription());
        user.setPassword(creatUserParam.getPassword());
        user.setUsername(creatUserParam.getUsername());
        user.setCreatTime(System.currentTimeMillis());
        userMapper.save(user);
        DbManger.commit();
        log.info("创建{} 用户",creatUserParam.getUsername());
        return user;
    }

    @Override
    public void applicationRun() throws Exception {

        OsUser root = userMapper.findById("root");
        if (root == null) {
            CreatUserParam param = new CreatUserParam();
            param.setUsername("root");
            param.setPassword("root");
            param.setDescription("root 用户");
            creatUser(param);
        }

    }
}
