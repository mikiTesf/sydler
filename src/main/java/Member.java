import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

@DatabaseTable (tableName = "member_info")
class Member {
    private static Dao<Member, Integer> memberDao;

    @DatabaseField (id = true)
    private int id;
    @DatabaseField
    private String fullName;
    @DatabaseField
    private boolean canBeStage;
    @DatabaseField
    private boolean canRotateMic;
    @DatabaseField
    private boolean sundayException;
    @DatabaseField
    private boolean canAssist2ndHall;

    static {
        try {
            memberDao = DaoManager.createDao(DBConnection.getConnectionSource(), Member.class);
            TableUtils.createTableIfNotExists(DBConnection.getConnectionSource(), Member.class);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // a no-arg constructor (required by ORMLITE - I don't know the reason)
    Member() { }

    static Dao<Member, Integer> getDao() {
        return memberDao;
    }

    String getFullName() {
        return fullName;
    }

    void setFullName(String fullName) {
        this.fullName = fullName;
    }

    int getId() {
        return id;
    }

    boolean canBeStage() {
        return canBeStage;
    }

    void setCanBeStage(boolean canBeStage) {
        this.canBeStage = canBeStage;
    }

    boolean canRotateMic() {
        return canRotateMic;
    }

    void setCanRotateMic(boolean canRotateMic) {
        this.canRotateMic = canRotateMic;
    }

    boolean canBeSecondHall() {
        return canAssist2ndHall;
    }

    void setCanAssist2ndHall(boolean canAssist2ndHall) {
        this.canAssist2ndHall = canAssist2ndHall;
    }

    boolean hasSundayException() {
        return sundayException;
    }

    void setSundayException(boolean sundayException) {
        this.sundayException = sundayException;
    }

    void save() {
        try {
            memberDao.create(this);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
