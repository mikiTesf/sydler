package view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileWriter;
import java.io.IOException;

import controller.Initializer;

public class GeneratorSettings extends JFrame {
    private JPanel panel1;
    private JCheckBox controlCounterCheckbox;
    private JCheckBox control2ndHallChooserCheckbox;
    private JTextArea checkBoxPurposeDescribingTextArea;

    GeneratorSettings () {
        checkBoxPurposeDescribingTextArea.setEditable(false);

        boolean countAllAppearances       = Initializer.settings.getBoolean(Initializer.COUNT_FROM_ALL_KEY);
        boolean choose2ndHallFrom1stRound = Initializer.settings.getBoolean(Initializer.CHOOSE_FROM_1ST_ROUND_KEY);

        controlCounterCheckbox.setSelected(countAllAppearances);
        control2ndHallChooserCheckbox.setSelected(choose2ndHallFrom1stRound);

        addWindowListener(new WindowListener() {
            @Override
            public void windowClosing(WindowEvent e) {
                final boolean COUNT_FROM_ALL_NEW_VALUE        = controlCounterCheckbox.isSelected();
                final boolean CHOOSE_FROM_1ST_ROUND_NEW_VALUE = control2ndHallChooserCheckbox.isSelected();

                Initializer.settings.remove(Initializer.COUNT_FROM_ALL_KEY);
                Initializer.settings.remove(Initializer.CHOOSE_FROM_1ST_ROUND_KEY);
                Initializer.settings.put(Initializer.COUNT_FROM_ALL_KEY, COUNT_FROM_ALL_NEW_VALUE);
                Initializer.settings.put(Initializer.CHOOSE_FROM_1ST_ROUND_KEY, CHOOSE_FROM_1ST_ROUND_NEW_VALUE);

                try (FileWriter writer = new FileWriter(Initializer.settingsFile)) {
                    writer.write(Initializer.settings.toString());
                    writer.close();
                } catch (IOException _e) {
                    _e.printStackTrace();
                }
            }

            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {}
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });

        setContentPane(panel1);
        setAlwaysOnTop(true);
        pack();
        setResizable(false);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }
}
