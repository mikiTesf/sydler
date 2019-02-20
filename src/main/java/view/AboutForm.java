package view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class AboutForm extends JFrame {

    private JPanel panel;
    private JLabel programNameLabel;
    private JLabel buildDateLabel;
    private JLabel developerNameLabel;
    private JLabel developerEmailLabel;
    private JLabel whatsNewInSydlerLabel;
    private JLabel newFeaturesListLabel;

    AboutForm (JFrame frame) {
        this.setContentPane(panel);
        setTitle("About " + MessagesAndTitles.PROGRAM_NAME_LABEL);
        programNameLabel.setText(MessagesAndTitles.PROGRAM_NAME_LABEL);
        buildDateLabel.setText(MessagesAndTitles.BUILD_DATE_LABEL);
        developerNameLabel.setText("Developed by: Mikyas Tesfamichael");
        developerEmailLabel.setText("(mickyasTesfamichael@gmail.com)");
        whatsNewInSydlerLabel.setText(MessagesAndTitles.WHATS_NEW_TAB_TITLE);
        newFeaturesListLabel.setText(MessagesAndTitles.WHATS_NEW_IN_SYDLER_LIST);

        programNameLabel.setFont(new Font("", Font.BOLD, 36));
        buildDateLabel.setFont(new Font("", Font.PLAIN, 16));
        developerNameLabel.setFont(new Font("", Font.PLAIN, 16));
        developerEmailLabel.setFont(new Font("", Font.PLAIN, 13));
        whatsNewInSydlerLabel.setFont(new Font("", Font.BOLD, 20));
        newFeaturesListLabel.setFont(new Font("", Font.PLAIN, 14));

        setSize(new Dimension(400, 250));
        setResizable(false);
        setAlwaysOnTop(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.setEnabled(true);
            }
        });
    }

    JFrame getFrame () {
        return this;
    }
}
