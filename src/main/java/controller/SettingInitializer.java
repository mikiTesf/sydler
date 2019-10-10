package controller;

import domain.Settings;

import java.sql.SQLException;

public class SettingInitializer {

    public static Settings SETTINGS;

    static {
        try {
            SETTINGS = Settings.getSettingsFromDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static final boolean KEY_COUNT_FROM_ALL = SETTINGS.isCountFromAllRoles();
    public static final boolean KEY_CHOOSE_FROM_1ST_ROUND = SETTINGS.isChooseHall2MemberFrom1stRound();
}
