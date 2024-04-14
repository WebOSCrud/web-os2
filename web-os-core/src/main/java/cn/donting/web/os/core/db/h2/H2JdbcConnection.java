package cn.donting.web.os.core.db.h2;

import org.h2.engine.Session;
import org.h2.jdbc.JdbcConnection;

import java.sql.SQLException;
import java.util.Properties;

public class H2JdbcConnection extends JdbcConnection {
    public H2JdbcConnection(String url, Properties info, String user, Object password, boolean forbidCreation) throws SQLException {
        super(url, info, user, password, forbidCreation);
    }

    public H2JdbcConnection(JdbcConnection clone) {
        super(clone);
    }

    public H2JdbcConnection(Session session, String user, String url) {
        super(session, user, url);
    }
}
