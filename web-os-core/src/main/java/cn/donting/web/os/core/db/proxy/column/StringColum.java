package cn.donting.web.os.core.db.proxy.column;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class StringColum extends EntityColumn<String> {

    public StringColum(Class entityClass, Method columMethod) {
        super(entityClass, columMethod);
    }

    @Override
    public String getSqlValue(Object entity) throws Exception {
        Object invoke = columMethod.invoke(entity);
        if(invoke==null){
            return null;
        }
        return invoke.toString();
    }
}
