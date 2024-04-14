package cn.donting.web.os.core.db.proxy.column;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;

public class StringColum extends EntityColumn<String> {

    public StringColum(Class entityClass, Method columMethod) {
        super(entityClass, columMethod);
    }



    @Override
    public String getValueSqlValue(Object value) throws Exception {
        return "'"+value+"'";
    }

    @Override
    public String getTableCreateColum()  {
        return columName+" VARCHAR(128)";
    }

    @Override
    public String getEntityValue(ResultSet resultSet) throws Exception {
        return resultSet.getString(columName);
    }
}
