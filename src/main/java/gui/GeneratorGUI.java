package gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

class GeneratorGUI extends JFrame {
    private GeneratorGUI() {
        setTitle("Sound System Schedule Generator");
        JTabbedPane tabbedPane = new JTabbedPane();
        // Generation management panel
        JPanel generate = new JPanel(new GridLayout(2,1));
        // Sub-Panels under 'generate'
        JPanel firstRow  = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel secondRow = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JLabel firstDayOfWeekLabel  = new JLabel("<html>First day<br/>of week</html>");
        JLabel congregationDayLabel = new JLabel("Congregation day");
        JLabel monthsLabel          = new JLabel("Months");

        JTextField firstWeekDayField    = new JTextField(12);
        JTextField congregationDayField = new JTextField(12);
        JTextField monthsField          = new JTextField(3);

        JButton generateButton = new JButton("Generate");

        firstRow.add(firstDayOfWeekLabel);  firstRow.add(firstWeekDayField);
        firstRow.add(congregationDayLabel); firstRow.add(congregationDayField);
        secondRow.add(monthsLabel); secondRow.add(monthsField);
        secondRow.add(generateButton);

        generate.add(firstRow); generate.add(secondRow);

        // **************************** 2nd Tab ****************************

        JPanel memberManagement = new JPanel(new GridLayout(2, 1));
        memberManagement.add(new JButton());
        memberManagement.add(new JButton());

        Border lineBorder = new LineBorder(Color.BLACK, 1);
        TitledBorder titledBorder = new TitledBorder(lineBorder, "Hello", TitledBorder.LEFT,
                TitledBorder.BELOW_BOTTOM, new Font("Hack", Font.BOLD, 12), Color.BLACK);
        titledBorder.setTitleJustification(TitledBorder.CENTER);
        titledBorder.setTitlePosition(TitledBorder.DEFAULT_POSITION);
        memberManagement.setBorder(titledBorder);

        tabbedPane.add("Generate", generate);
        tabbedPane.add("Member", memberManagement);

        add(tabbedPane);

        pack();
        setLocation(622, 354);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        GeneratorGUI gui = new GeneratorGUI();
    }
}
