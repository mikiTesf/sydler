package domain;

import java.io.File;
import java.sql.SQLException;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

class DBConnection {
    private static final ConnectionSource connectionSource = initializeConnectionSource();

    private static ConnectionSource initializeConnectionSource () {
        String dbURL = "jdbc:sqlite:database" + File.separator + "data.sqlite3";
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
