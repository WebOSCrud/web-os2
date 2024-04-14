package cn.donting.web.os.core.db.proxy;

import cn.donting.web.os.core.db.DbManger;
import cn.donting.web.os.core.db.annotation.Table;
import cn.donting.web.os.core.db.mapper.BaseMapper;
import cn.donting.web.os.core.db.proxy.column.EntityColumn;
import cn.donting.web.os.core.db.proxy.column.LongColum;
import cn.donting.web.os.core.db.proxy.column.StringColum;
import cn.donting.web.os.core.util.Lists;
import cn.donting.web.os.core.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class BaseMapperHandler implements InvocationHandler, BaseMapper {
    private final Class<? extends BaseMapper> mapperClass;

    private Class entityClass;
    private Class entityIdClass;

    private List<EntityColumn> entityColumns;
    private EntityColumn idEntityColumns;

    private Table table;
    private String tableName;

    private boolean checkTable = true;


    public BaseMapperHandler(Class<? extends BaseMapper> mapperClass) throws SQLException {
        this.mapperClass = mapperClass;
        table = mapperClass.getAnnotation(Table.class);
        if (table == null) {
            table = DefTable.class.getAnnotation(Table.class);
        }
        // 获取 BaseMapper 接口
        Type[] interfaces = mapperClass.getGenericInterfaces();
        for (Type type : interfaces) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                if (parameterizedType.getRawType() == BaseMapper.class) {
                    // 获取 BaseMapper 接口的泛型参数
                    try {
                        Type[] typeArguments = parameterizedType.getActualTypeArguments();
                        entityClass = mapperClass.getClassLoader().loadClass(typeArguments[0].getTypeName());
                        entityIdClass = mapperClass.getClassLoader().loadClass(typeArguments[1].getTypeName());
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
        entityColumns = new ArrayList<>();
        setEntityColumn(entityClass, entityColumns);
        for (EntityColumn entityColumn : entityColumns) {
            if (entityColumn.isId()) {
                idEntityColumns = entityColumn;
                break;
            }
        }
        tableName = table.name();
        if (tableName.equals("")) {
            tableName = StringUtil.toUnderscore(entityClass.getSimpleName());
        }
    }


    private void checkTable() {
        try {
            if (checkTable) {
                checkTable = false;
                creatTable();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void creatTable() throws SQLException {
        Connection connection = DbManger.getConnection();

        Statement statement = connection.createStatement();

        String selectTableSql = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC' AND TABLE_NAME = '{tableName}'";
        selectTableSql = selectTableSql.replace("{tableName}", tableName);
        ResultSet resultSet = statement.executeQuery(selectTableSql);
        //存在表
        if (resultSet.next()) {
            return;
        }
        StringBuilder tableCreatSql = new StringBuilder();
        tableCreatSql.append("CREATE TABLE ").append(tableName).append("(\n");
        tableCreatSql.append("   ").append(entityColumns.get(0).getTableCreateColum());
        for (int i = 1; i < entityColumns.size(); i++) {
            EntityColumn entityColumn = entityColumns.get(i);
            tableCreatSql.append(",\n   ").append(entityColumn.getTableCreateColum());
            if (entityColumn.isId()) {
                tableCreatSql.append(" PRIMARY KEY");
            }
        }
        tableCreatSql.append("\n)");
        log.info("\n" + tableCreatSql.toString());
        statement.executeUpdate(tableCreatSql.toString());

        DbManger.releaseConnection();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!isBaseMapperMethod(method)) {
            throw new Exception(method.getName() + " 不是BaseMapper所属");
        }
        checkTable();
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
        String sqlTem = "insert into {tableName}({columns}) values ({values})";
        try {
            List<String> columns = new ArrayList<>();
            List<String> values = new ArrayList<>();
            for (EntityColumn entityColumn : entityColumns) {
                String sqlValue = entityColumn.getEntitySqlValue(entity);
                if (sqlValue != null) {
                    columns.add(entityColumn.columName());
                    values.add(sqlValue);
                }
            }
            String columnsStr = Lists.toString(columns);
            String valuesStr = Lists.toString(values);
            sqlTem = sqlTem.replace("{tableName}", tableName);
            sqlTem = sqlTem.replace("{columns}", columnsStr);
            sqlTem = sqlTem.replace("{values}", valuesStr);
            log.info(sqlTem);
            Connection connection = DbManger.getConnection();
            Statement statement = connection.createStatement();
            int i = statement.executeUpdate(sqlTem, Statement.RETURN_GENERATED_KEYS);
            if (idEntityColumns.getEntitySqlValue(entity)==null && i > 0 && idEntityColumns instanceof LongColum) {
                // 查询最后插入的自增ID
                try (ResultSet resultSet = statement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        long id = resultSet.getLong(1);
                        idEntityColumns.getColumSetMethod().invoke(entity,id);
                    } else {
                        System.out.println("未能获取插入的自增ID");
                    }
                }
            }
            return i;
        } catch (Exception ex) {
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
        try {
            String sqlValue = idEntityColumns.getValueSqlValue(id);
            String selectSql = "select * from {tableName} where {idName}={sqlValue}";
            selectSql = selectSql.replace("{tableName}", tableName);
            selectSql = selectSql.replace("{idName}", idEntityColumns.columName());
            selectSql = selectSql.replace("{sqlValue}", sqlValue);
            Statement statement = DbManger.getConnection().createStatement();
            log.info(selectSql);
            ResultSet resultSet = statement.executeQuery(selectSql);
            if (!resultSet.next()) {
                return null;
            }
            Object entity = entityClass.newInstance();
            for (EntityColumn entityColumn : entityColumns) {
                Object entityValue = entityColumn.getEntityValue(resultSet);
                if(entityValue!=null){
                    entityColumn.getColumSetMethod().invoke(entity, entityValue);
                }
            }
            return entity;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public int saveAll(List entities) {
        return 0;
    }

    private void setEntityColumn(Class entityClass, List<EntityColumn> entityColumns) {
        if (entityClass.equals(Object.class)) {
            return;
        }
        Method[] methods = entityClass.getMethods();
        for (Method method : methods) {
            if (method.getDeclaringClass().equals(Object.class)) {
                continue;
            }
            if (method.getName().startsWith("get")) {
                Class<?> returnType = method.getReturnType();
                if (returnType.equals(String.class)) {
                    StringColum stringColum = new StringColum(entityClass, method);
                    entityColumns.add(stringColum);
                }
                if (returnType.equals(Long.class)) {
                    LongColum colum = new LongColum(entityClass, method);
                    entityColumns.add(colum);
                }
            }
        }
    }

    @Table
    private static class DefTable {
    }
}
