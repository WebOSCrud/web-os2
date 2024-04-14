package cn.donting.web.os.core.db.proxy.column;

import java.lang.reflect.Method;

public class LongColum extends EntityColumn<Long>{

    public LongColum(Class entityClass, Method columMethod) {
        super(entityClass, columMethod);
    }

    @Override
    public String getSqlValue(Object entity) {

        return null;
    }
}
