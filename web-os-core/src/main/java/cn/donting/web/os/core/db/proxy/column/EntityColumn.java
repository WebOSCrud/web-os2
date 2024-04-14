package cn.donting.web.os.core.db.proxy.column;

import cn.donting.web.os.core.db.annotation.Table;
import cn.donting.web.os.core.db.proxy.BaseMapperHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 实体的列
 *
 * @param <T> 列的类型
 */
public abstract class EntityColumn<T> {
    protected final Class entityClass;
    protected final Method columMethod;
    protected final String columName;

    protected final Table table;

    public EntityColumn(Class<?> entityClass, Method columMethod) {
        this.entityClass = entityClass;
        this.columMethod = columMethod;
        Table table = entityClass.getAnnotation(Table.class);
        if (table == null) {
            this.table = DefTable.class.getAnnotation(Table.class);
        } else {
            this.table = table;
        }
        columName = toColumName(columMethod);
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
     *
     * @param entity
     * @return
     */
    public abstract String getSqlValue(Object entity) throws Exception;

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

    @Override
    public String toString() {
        return columName+"["+columMethod.getReturnType().getSimpleName()+"]";
    }

    @Table
    private static class DefTable {
    }
}
