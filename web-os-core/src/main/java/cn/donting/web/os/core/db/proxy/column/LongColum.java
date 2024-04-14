package cn.donting.web.os.core.db.proxy.column;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class LongColum extends EntityColumn<Long> {

    public LongColum(Class entityClass, Method columGetMethod) {
        super(entityClass, columGetMethod);
    }


    @Override
    public String getValueSqlValue(Object value) throws Exception {
        return value.toString();
    }

    @Override
    public String getTableCreateColum() {
        if(isId()){
            return columName + " BIGINT AUTO_INCREMENT";
        }
        return columName + " BIGINT";
    }

    @Override
    public Long getEntityValue(ResultSet resultSet) throws Exception {
        ResultSetMetaData metaData=resultSet.getMetaData();
        int columnIndex = resultSet.findColumn(columName);
        if(metaData.isNullable(columnIndex)==ResultSetMetaData.columnNullable){
            return null;
        }
        long aLong = resultSet.getLong(columnIndex);
        return aLong;
    }
}
