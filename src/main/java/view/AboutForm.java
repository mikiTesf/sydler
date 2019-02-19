package view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class AboutForm extends JFrame {

    private JPanel panel;
    private JLabel nameAndVersionLabel;
    private JLabel buildDateLabel;
    private JLabel developerNameLabel;
    private JLabel developerEmailLabel;
    private JLabel whatsNewInSydlerLabel;
    private JLabel newFeaturesListLabel;

    AboutForm (JFrame frame) {
        this.setContentPane(panel);
        setTitle("About Sydler");
        nameAndVersionLabel.setText(TitlesAndLabels.NAME_AND_VERSION_LABEL);
        buildDateLabel.setText(TitlesAndLabels.BUILD_DATE_LABEL);
        developerNameLabel.setText(TitlesAndLabels.DEVELOPER_NAME_LABEL);
        developerEmailLabel.setText(TitlesAndLabels.DEVELOPER_EMAIL_LABEL);
        whatsNewInSydlerLabel.setText(TitlesAndLabels.WHATS_NEW_TAB_TITLE);
        newFeaturesListLabel.setText(TitlesAndLabels.WHATS_NEW_IN_SYDLER_LIST);
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
