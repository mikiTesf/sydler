package view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import controller.SettingInitializer;
import domain.Settings;

class PreferenceForm extends JFrame {
    private JPanel panel1;
    private JCheckBox controlCounterCheckbox;
    private JCheckBox control2ndHallChooserCheckbox;
    private JLabel preferenceDetailsLabel;
    private JLabel currentSettingsLabel;

    PreferenceForm(JFrame frame) {
        setTitle(MessagesAndTitles.PREFERENCES_FRAME_TITLE);
        preferenceDetailsLabel.setText(MessagesAndTitles.PREFERENCES_DETAILS_LABEL);
        preferenceDetailsLabel.setFont(new Font("", Font.PLAIN, 15));
        currentSettingsLabel.setText(MessagesAndTitles.CURRENT_SETTINGS_LABEL);
        controlCounterCheckbox.setText(MessagesAndTitles.COUNT_PREFERENCE_CHECKBOX_LABEL);
        controlCounterCheckbox.setFont(new Font("", Font.PLAIN, 14));
        control2ndHallChooserCheckbox.setText(MessagesAndTitles.HALL2_PREFERENCE_CHECK_BOX_LABEL);
        control2ndHallChooserCheckbox.setFont(new Font("", Font.PLAIN, 14));

        boolean countAllAppearances       = SettingInitializer.KEY_COUNT_FROM_ALL;
        boolean choose2ndHallFrom1stRound = SettingInitializer.KEY_CHOOSE_FROM_1ST_ROUND;

        controlCounterCheckbox.setSelected(countAllAppearances);
        control2ndHallChooserCheckbox.setSelected(choose2ndHallFrom1stRound);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.setEnabled(false);

                SettingInitializer.SETTINGS.setCountFromAllRoles(controlCounterCheckbox.isSelected());
                SettingInitializer.SETTINGS.setChooseHall2MemberFrom1stRound(control2ndHallChooserCheckbox.isSelected());

                try {
                    Settings.settingsDao.update(SettingInitializer.SETTINGS);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        setContentPane(panel1);
        setAlwaysOnTop(true);
        pack();
        setResizable(false);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }
}
