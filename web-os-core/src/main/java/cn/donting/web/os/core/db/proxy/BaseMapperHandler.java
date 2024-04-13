package cn.donting.web.os.core.db.proxy;

import cn.donting.web.os.core.db.mapper.BaseMapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class BaseMapperHandler implements InvocationHandler, BaseMapper {
    private final Class<? extends BaseMapper> tragetClass;

    private final Class entityClass;
    private final Class entityIdClass;
    public BaseMapperHandler(Class<? extends BaseMapper> tragetClass) {
        this.tragetClass = tragetClass;
// 获取 BaseMapper 接口
        Type[] interfaces = tragetClass.getGenericInterfaces();
        for (Type type : interfaces) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                if (parameterizedType.getRawType() == BaseMapper.class) {
                    // 获取 BaseMapper 接口的泛型参数
                    Type[] typeArguments = parameterizedType.getActualTypeArguments();

                    for (Type typeArgument : typeArguments) {
                        System.out.println("BaseMapper 接口的泛型参数: " + typeArgument.getTypeName());
                    }
                }
            }
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!isBaseMapperMethod(method)) {
            throw new Exception(method.getName() + " 不是BaseMapper所属");
        }
        if (method.getName().equals("save")) {
            return save(args[0]);
        }
        if (method.getName().equals("deleteById")) {
            return deleteById(args[0]);
        }
        if (method.getName().equals("findAll")) {
            return findAll();
        }
        if (method.getName().equals("findById")) {
            return findById(args[0]);
        }
        if (method.getName().equals("saveAll")) {
            return findById(args[0]);
        }
        throw new Exception(method.getName() + " 未实现");
    }

    protected boolean isBaseMapperMethod(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        return declaringClass.equals(BaseMapper.class);
    }

    @Override
    public int save(Object entity) {
        return 0;
    }

    @Override
    public int deleteById(Object id) {
        return 0;
    }

    @Override
    public List findAll() {
        return null;
    }

    @Override
    public Object findById(Object id) {
        return null;
    }

    @Override
    public int saveAll(List entities) {
        return 0;
    }
}
