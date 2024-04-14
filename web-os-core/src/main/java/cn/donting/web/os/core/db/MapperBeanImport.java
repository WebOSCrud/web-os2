package cn.donting.web.os.core.db;

import cn.donting.web.os.core.db.annotation.Mapper;
import cn.donting.web.os.core.db.mapper.BaseMapper;
import cn.donting.web.os.core.db.mapper.UserMapper;
import cn.donting.web.os.core.db.proxy.BaseMapperHandler;
import cn.donting.web.os.web.ioc.annotation.BeanImport;

import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapperBeanImport implements BeanImport {
    @Override
    public Map<String,Object> importBeans(List<Class> packageScanClass) throws Exception {
        List<Class> mappers = packageScanClass.stream().filter(c -> {
            if (c.isInterface() && c.getAnnotation(Mapper.class) != null) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        Map<String,Object> beans = new HashMap<>();

        for (Class<? extends BaseMapper> mapper : mappers) {
            BaseMapperHandler dynamicProxy = new BaseMapperHandler(mapper);
            // 创建动态代理
            BaseMapper baseMapper = (BaseMapper) Proxy.newProxyInstance(
                    UserMapper.class.getClassLoader(),
                    new Class[]{mapper},
                    dynamicProxy
            );
            beans.put(mapper.getSimpleName(),baseMapper);
        }
        return beans;
    }
}
