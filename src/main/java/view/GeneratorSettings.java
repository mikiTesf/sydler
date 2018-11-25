package view;

import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONObject;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GeneratorSettings extends JFrame {
    private JPanel panel1;
    private JCheckBox control2ndHallChooserCheckbox;
    private JCheckBox controlCounterCheckbox;
    private JTextArea checkBoxPurposeDeskcribingTextArea;
    private String COUNT_FROM_ALL        = "countFromAll";
    private String CHOOSE_FROM_1ST_ROUND = "choose2ndHallFromFirstRound";

    GeneratorSettings () {
        checkBoxPurposeDeskcribingTextArea.setEditable(false);

        setContentPane(panel1);
        pack();
        setResizable(false);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        File settingsFile = new File("settings.json");
        JSONObject settings = new JSONObject();
        if (!settingsFile.exists()) {
            settings.put(COUNT_FROM_ALL, false);
            settings.put(CHOOSE_FROM_1ST_ROUND, true);

            try (FileWriter writer = new FileWriter(settingsFile)) {
                writer.write(settings.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        boolean countAllAppearances       = Boolean.parseBoolean(settings.get(COUNT_FROM_ALL).toString());
        boolean choose2ndHallFrom1stRound = Boolean.parseBoolean(settings.get(CHOOSE_FROM_1ST_ROUND).toString());

        if (countAllAppearances) controlCounterCheckbox.setSelected(true);

        if (choose2ndHallFrom1stRound) control2ndHallChooserCheckbox.setSelected(true);
        else control2ndHallChooserCheckbox.setSelected(false);

        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {

            }

            @Override
            public void windowClosed(WindowEvent e) {
                JSONObject settings = new JSONObject();
                settings.put(COUNT_FROM_ALL, controlCounterCheckbox.isSelected());
                settings.put(CHOOSE_FROM_1ST_ROUND, control2ndHallChooserCheckbox.isSelected());

                File settingsFile = new File("settings.json");
                try (FileWriter writer = new FileWriter(settingsFile)) {
                    writer.write(settings.toString());
                } catch (IOException _e) {
                    _e.printStackTrace();
                }
            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
    }
}
