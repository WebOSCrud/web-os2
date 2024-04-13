package cn.donting.web.os.core;

import cn.donting.web.os.core.db.entity.OsUser;
import cn.donting.web.os.core.db.mapper.BaseMapper;
import cn.donting.web.os.core.db.mapper.UserMapper;
import cn.donting.web.os.core.db.proxy.BaseMapperHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DbTest {
    public static void main(String[] args) {
        BaseMapperHandler dynamicProxy = new BaseMapperHandler(UserMapper.class);

        // 创建动态代理
        UserMapper myInterface = (UserMapper) Proxy.newProxyInstance(
                UserMapper.class.getClassLoader(),
                new Class[]{UserMapper.class},
                dynamicProxy
        );
        myInterface.save(new OsUser());
    }

}
