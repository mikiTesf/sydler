package view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class AboutForm extends JFrame {

    private JPanel panel;
    @SuppressWarnings("unused")
    private JTextPane newOnThisVersionDescriptionTextPane;

    AboutForm (JFrame frame) {
        this.setContentPane(panel);
        setTitle("About Schedule Generator");
        setSize(new Dimension(350, 200));
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
