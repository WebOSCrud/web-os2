package cn.donting.web.os.core.db.h2;

import org.h2.Driver;
import org.h2.api.ErrorCode;
import org.h2.engine.Constants;
import org.h2.jdbc.JdbcConnection;
import org.h2.message.DbException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class H2Driver extends Driver {

    static {
        Driver.unload();
        try {
            DriverManager.registerDriver(new H2Driver());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (url == null) {
            throw DbException.getJdbcSQLException(ErrorCode.URL_FORMAT_ERROR_2, null, Constants.URL_FORMAT, null);
        } else if (url.startsWith(Constants.START_URL)) {
            return new H2JdbcConnection(url, info, null, null, false);
        }
        return null;
    }
}
