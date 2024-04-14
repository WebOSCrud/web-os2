package cn.donting.web.os.core.db.proxy.column;

import cn.donting.web.os.core.db.annotation.Table;
import cn.donting.web.os.core.db.proxy.BaseMapperHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;

/**
 * 实体的列
 *
 * @param <T> 列的类型
 */
public abstract class EntityColumn<T> {
    protected final Class entityClass;
    protected final Method columGetMethod;
    protected final Method columSetMethod;
    protected final String columName;

    protected final Table table;

    public EntityColumn(Class<?> entityClass, Method columGetMethod) {
        this.entityClass = entityClass;
        this.columGetMethod = columGetMethod;
        Table table = entityClass.getAnnotation(Table.class);
        if (table == null) {
            this.table = DefTable.class.getAnnotation(Table.class);
        } else {
            this.table = table;
        }
        columName = toColumName(columGetMethod);
        String name = columGetMethod.getName();
        String setMethodName = "s" + name.substring(1);
        try {
            columSetMethod = entityClass.getMethod(setMethodName, columGetMethod.getReturnType());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public String columName() {
        return columName;
    }

    public boolean isId() {
        if (table.id().equals("")) {
            return columName.equals("id");
        }
        return columName.equals(table.id());
    }

    /**
     * 从实体 读取  到sql中设置的值
     * @param entity
     * @return
     */
    public String getEntitySqlValue(Object entity) throws Exception {
        Object value = columGetMethod.invoke(entity);
        if (value == null) {
            return null;
        }
        return getValueSqlValue(value);
    }

    /**
     * 一个 value 设置到sql 中的值
     * 字符串+ ''
     * @param value
     * @return
     * @throws Exception
     */
    public abstract String getValueSqlValue(Object value) throws Exception;

    /**
     * 创建 表时的列
     *
     * @return
     */
    public abstract String getTableCreateColum();


    public abstract T getEntityValue(ResultSet resultSet) throws Exception;

    protected String toColumName(Method columMethod) {
        String name = columMethod.getName();
        //去掉get
        name = name.substring(3);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (i == 0) {
                builder.append(Character.toLowerCase(c));
                continue;
            }
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    builder.append('_');
                }
                builder.append(Character.toLowerCase(c));
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    public Method getColumGetMethod() {
        return columGetMethod;
    }

    public Method getColumSetMethod() {
        return columSetMethod;
    }

    @Override
    public String toString() {
        return columName + "[" + columGetMethod.getReturnType().getSimpleName() + "]";
    }

    @Table
    private static class DefTable {
    }
}
