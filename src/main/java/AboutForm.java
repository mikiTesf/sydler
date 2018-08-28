import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;

class AboutForm extends JFrame {

    private JPanel panel1;

    AboutForm () {
        this.setContentPane(panel1);
        setTitle("About Schedule Generator");
        setSize(new Dimension(280, 170));
        setResizable(false);
        setAlwaysOnTop(true);
    }

    JFrame getFrame () {
        return this;
    }
}
