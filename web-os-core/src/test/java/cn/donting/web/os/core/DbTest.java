package cn.donting.web.os.core;

import cn.donting.web.os.core.db.entity.OsUser;
import cn.donting.web.os.core.db.mapper.BaseMapper;
import cn.donting.web.os.core.db.mapper.UserMapper;
import cn.donting.web.os.core.db.proxy.BaseMapperHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbTest {
    public static void main(String[] args) throws SQLException {
        long l = System.currentTimeMillis();
        BaseMapperHandler dynamicProxy = new BaseMapperHandler(UserMapper.class);
        // 创建动态代理
        UserMapper myInterface = (UserMapper) Proxy.newProxyInstance(
                UserMapper.class.getClassLoader(),
                new Class[]{UserMapper.class},
                dynamicProxy
        );

        OsUser entity = new OsUser();
        entity.setUsername("123");
        entity.setPassword("123");
        entity.setDescription("描述");
        entity.setCreatTime(System.currentTimeMillis());
        myInterface.save(entity);
//        conn1.
    }

}
