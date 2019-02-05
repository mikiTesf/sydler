package domain;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;

class DBConnection {
    private static final ConnectionSource connectionSource = initializeConnectionSource();

    private static ConnectionSource initializeConnectionSource () {
        String dbURL = "jdbc:sqlite:database/members.db";
        try {
            return new JdbcConnectionSource(dbURL);
        } catch (SQLException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    static ConnectionSource getConnectionSource() {
        return connectionSource;
    }
}
