package view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import java.awt.Dimension;

class AboutForm extends JFrame {

    private JPanel panel;
    @SuppressWarnings("unused")
    private JTextPane columnHeadersInTheTextPane;

    AboutForm () {
        this.setContentPane(panel);
        setTitle("About Schedule Generator");
        setSize(new Dimension(350, 200));
        setResizable(false);
        setAlwaysOnTop(true);
    }

    JFrame getFrame () {
        return this;
    }
}
