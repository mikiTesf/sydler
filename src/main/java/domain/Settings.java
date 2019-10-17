package domain;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

@DatabaseTable(tableName = "settings")
public class Settings {

    public static Dao<Settings, Integer> settingsDao;

    @DatabaseField (generatedId = true)
    private int id;

    @DatabaseField(defaultValue = "true")
    private boolean countFromAllRoles;

    @DatabaseField(defaultValue = "true")
    private boolean chooseHall2MemberFrom1stRound;

    static {
        try {
            settingsDao = DaoManager.createDao(DBConnection.getConnectionSource(), Settings.class);
            TableUtils.createTableIfNotExists(DBConnection.getConnectionSource(), Settings.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // this no-arg constructor is required by ORMLITE
    Settings() {}

    private Settings(boolean countFromAllRoles, boolean chooseHall2MemberFrom1stRound) {
        this.countFromAllRoles = countFromAllRoles;
        this.chooseHall2MemberFrom1stRound = chooseHall2MemberFrom1stRound;
    }

    public boolean isCountFromAllRoles() {
        return countFromAllRoles;
    }

    public void setCountFromAllRoles(boolean countFromAllRoles) {
        this.countFromAllRoles = countFromAllRoles;
    }

    public boolean isChooseHall2MemberFrom1stRound() {
        return chooseHall2MemberFrom1stRound;
    }

    public void setChooseHall2MemberFrom1stRound(boolean chooseHall2MemberFrom1stRound) {
        this.chooseHall2MemberFrom1stRound = chooseHall2MemberFrom1stRound;
    }

    public static Settings getSettingsFromDB() throws SQLException {
        // when running sydler for the first time, the settings table will have 0 records
        // and so this method will throw an IndexOutOfBoundsException resulting sydler in
        // exiting. This event may also occur if the database is accidentally deleted. Therefore,
        // it is necessary that the return from the `queryForAll()` method is checked
        List<Settings> settingsFromDB = settingsDao.queryForAll();

        Settings settings = settingsFromDB.size() == 0 ? null : settingsFromDB.get(0);

        if (settings == null) {
            settings = new Settings(true, true);
            settingsDao.create(settings);
        }

        return settings;
    }
}
