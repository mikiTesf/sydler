package view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class AboutForm extends JFrame {

    private JPanel panel;
    @SuppressWarnings("unused")
    private JLabel whatsNewLabel;

    AboutForm (JFrame frame) {
        this.setContentPane(panel);
        setTitle("About Schedule Generator");
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
