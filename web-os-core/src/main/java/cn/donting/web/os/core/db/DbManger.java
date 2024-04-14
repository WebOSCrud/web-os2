package cn.donting.web.os.core.db;

import cn.donting.web.os.core.db.h2.H2Driver;
import cn.donting.web.os.core.properties.DbProperties;
import cn.donting.web.os.web.annotation.Autowired;
import cn.donting.web.os.web.annotation.Service;
import cn.donting.web.os.web.ioc.ApplicationRunner;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Service
public class DbManger implements ApplicationRunner {

    private static DbProperties staticDbProperties;

    private static int MAX_CONNECTION=5;
    @Autowired
    DbProperties dbProperties;
    /**
     * 可用的连接
     */
    private static List<Connection> connections = new ArrayList<>();
    /**
     * 所有的连接
     */
    private static List<Connection> allConnections = new ArrayList<>();

    private static ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();

    /**
     * 获取连接
     * @return
     */
    public static Connection getConnection() {
        Connection connection = connectionThreadLocal.get();
        if (connection != null) {
            return connection;
        }
        synchronized (DbManger.class){
            if (connections.size()>0) {
                connection=connections.remove(0);
            }else {
                if (allConnections.size()<MAX_CONNECTION) {
                    connection= getConnection();
                    allConnections.add(connection);
                }else {
                    while (connections.size()==0){
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    connection=connections.remove(0);
                    allConnections.add(connection);
                }
            }
            connectionThreadLocal.set(connection);
            return connection;
        }
    }

    /**
     * 归还连接
     */
    public static void releaseConnection(){
        Connection connection = connectionThreadLocal.get();
        connectionThreadLocal.remove();
        connections.add(connection);
    }
    private  Connection connectionDb() throws SQLException {
        return DriverManager.getConnection(staticDbProperties.getUrl(), staticDbProperties.getUsername(), staticDbProperties.getPassword());
    }

    @Override
    public void applicationRun() throws Exception {
        DbManger.staticDbProperties = this.dbProperties;
        connectionDb();
    }
}
