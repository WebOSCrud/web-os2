package cn.donting.web.os.core.db.proxy;

import cn.donting.web.os.core.db.annotation.Table;
import cn.donting.web.os.core.db.mapper.BaseMapper;
import cn.donting.web.os.core.db.proxy.column.EntityColumn;
import cn.donting.web.os.core.db.proxy.column.LongColum;
import cn.donting.web.os.core.db.proxy.column.StringColum;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
@Slf4j
public class BaseMapperHandler implements InvocationHandler, BaseMapper {
    private final Class<? extends BaseMapper> tragetClass;

    private Class entityClass;
    private Class entityIdClass;

    private List<EntityColumn> entityColumns;

    private Table table;


    public BaseMapperHandler(Class<? extends BaseMapper> tragetClass) {
        this.tragetClass = tragetClass;
        table = tragetClass.getAnnotation(Table.class);
        if (table == null) {
            table = DefTable.class.getAnnotation(Table.class);
        }
        // 获取 BaseMapper 接口
        Type[] interfaces = tragetClass.getGenericInterfaces();
        for (Type type : interfaces) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                if (parameterizedType.getRawType() == BaseMapper.class) {
                    // 获取 BaseMapper 接口的泛型参数
                    try {
                        Type[] typeArguments = parameterizedType.getActualTypeArguments();
                        entityClass = tragetClass.getClassLoader().loadClass(typeArguments[0].getTypeName());
                        entityIdClass = tragetClass.getClassLoader().loadClass(typeArguments[1].getTypeName());
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
        entityColumns = new ArrayList<>();
        setEntityColumn(entityClass,entityColumns);

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
        try {
            for (EntityColumn entityColumn : entityColumns) {
                String sqlValue = entityColumn.getSqlValue(entity);
                log.info(entityColumn.columName() + " " + sqlValue);
            }
            return 0;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
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

    private void setEntityColumn(Class entityClass, List<EntityColumn> entityColumns) {
        if (entityClass.equals(Object.class)) {
            return;
        }
        Method[] declaredMethods = entityClass.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (Modifier.isPublic(declaredMethod.getModifiers()) && declaredMethod.getName().startsWith("get")) {
                Class<?> returnType = declaredMethod.getReturnType();
                if (returnType.equals(String.class)) {
                    StringColum stringColum = new StringColum(entityClass, declaredMethod);
                    entityColumns.add(stringColum);
                }
                if (returnType.equals(Long.class)) {
                    LongColum colum = new LongColum(entityClass, declaredMethod);
                    entityColumns.add(colum);
                }
            }
        }
        Class superclass = entityClass.getSuperclass();
        setEntityColumn(superclass,entityColumns);
    }

    @Table
    private static class DefTable {
    }
}
