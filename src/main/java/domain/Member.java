package domain;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

@DatabaseTable (tableName = "member_info")
public class Member {
    private static Dao<Member, Integer> memberDao;

    @DatabaseField (generatedId = true)
    private int id;
    @DatabaseField (canBeNull = false)
    private String firstName;
    @DatabaseField (canBeNull = false)
    private String lastName;
    @DatabaseField (canBeNull = false)
    private boolean hasDuplicateFirstName;
    @DatabaseField
    private boolean canBeStage;
    @DatabaseField
    private boolean canRotateMic;
    @DatabaseField
    private boolean canAssist2ndHall;
    @DatabaseField
    private boolean sundayException;

    static {
        try {
            memberDao = DaoManager.createDao(DBConnection.getConnectionSource(), Member.class);
            TableUtils.createTableIfNotExists(DBConnection.getConnectionSource(), Member.class);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // this no-arg constructor is required by ORMLITE
    public Member() {
        this.setHasDuplicateFirstName(false);
        this.setId(-1);
    }

    public static Dao<Member, Integer> getDao() {
        return memberDao;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean hasDuplicateFirstName() {
        return hasDuplicateFirstName;
    }

    public void setHasDuplicateFirstName(boolean hasDuplicateFirstName) {
        this.hasDuplicateFirstName = hasDuplicateFirstName;
    }

    public boolean canBeStage() {
        return canBeStage;
    }

    public void setCanBeStage(boolean canBeStage) {
        this.canBeStage = canBeStage;
    }

    public boolean canRotateMic() {
        return canRotateMic;
    }

    public void setCanRotateMic(boolean canRotateMic) {
        this.canRotateMic = canRotateMic;
    }

    public boolean canBe2ndHall() {
        return canAssist2ndHall;
    }

    public void setCanAssist2ndHall(boolean canAssist2ndHall) {
        this.canAssist2ndHall = canAssist2ndHall;
    }

    public boolean hasSundayException() {
        return sundayException;
    }

    public void setSundayException(boolean sundayException) {
        this.sundayException = sundayException;
    }

    public boolean save() {
        try {
            memberDao.create(this);
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static boolean remove (int id) {
        try {
            memberDao.deleteById(id);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
}
