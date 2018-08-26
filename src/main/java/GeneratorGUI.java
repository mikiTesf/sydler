import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class GeneratorGUI extends JFrame {

    private JTextField firstNameField;
    private JTextField lastNameField;
    private JCheckBox checkStageQualify;
    private JCheckBox checkMicQualify;
    private JCheckBox checkHall2Qualify;
    private JCheckBox checkSundayException;

    private GeneratorGUI() {
        setTitle("Sound System Schedule Generator");

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu saveMenu = new JMenu("Help");

        menuBar.add(fileMenu);
        menuBar.add(saveMenu);
        setJMenuBar(menuBar);

        JTabbedPane tabbedPane = new JTabbedPane();

        // **************************** 1st Tab ****************************

        // Generation management panel
        JPanel generate  = new JPanel(new GridLayout(2,1));
        // Sub-Panels under 'generate'
        JPanel firstRow  = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel secondRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        String[] dayNames = {"ሰኞ", "ማክሰኞ", "ዕሮብ", "ሐሙስ", "አርብ", "ቅዳሜ"};
        JTextField firstWeekDayField           = new JTextField(8);
        JComboBox<String> congregationDayField = new JComboBox<>(dayNames);
        JSpinner monthsField                   = new JSpinner(new SpinnerNumberModel(1, 1, null, 1));

        JComponent editor = monthsField.getEditor();
        JFormattedTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
        tf.setColumns(3);

        JLabel firstDayOfWeekLabel  = new JLabel("<html>first day<br/>of week</html>");
        firstDayOfWeekLabel.setLabelFor(firstWeekDayField);
        JLabel congregationDayLabel = new JLabel("<html>meeting day<br/>(midweek)<html>");
        congregationDayLabel.setLabelFor(congregationDayField);
        JLabel monthsLabel          = new JLabel("month(s)");
        monthsLabel.setLabelFor(monthsField);

        JButton generateButton = new JButton("Generate");

        firstRow.add(firstDayOfWeekLabel);  firstRow.add(firstWeekDayField);
        firstRow.add(congregationDayLabel); firstRow.add(congregationDayField);
        firstRow.add(monthsLabel);          firstRow.add(monthsField);
        secondRow.add(generateButton);

        generate.add(firstRow);
        generate.add(secondRow);

        // **************************** 2nd Tab ****************************

        JPanel memberManagement = new JPanel(new GridLayout(2, 1));

        JPanel addMemberPanel        = new JPanel(new GridLayout(3, 1));
        JPanel editDeleteMemberPanel = new JPanel(new GridLayout(1, 2));


        JPanel namePanel      = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel firstNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel lastNamePanel  = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        firstNameField = new JTextField(14);
        lastNameField  = new JTextField(14);

        JLabel firstNameLabel = new JLabel("<html>First<br/>Name</html>");
        firstNameLabel.setLabelFor(firstNameField);
        JLabel lastNameLabel  = new JLabel("<html>Last<br/>Name</html>");
        lastNameLabel.setLabelFor(lastNameField);

        firstNamePanel.add(firstNameLabel); firstNamePanel.add(firstNameField);
        lastNamePanel.add(lastNameLabel); lastNamePanel.add(lastNameField);

        namePanel.add(firstNamePanel); namePanel.add(lastNamePanel);

        Border borderForAddPanel    = new LineBorder(Color.LIGHT_GRAY, 1);
        TitledBorder addTitleBorder = new TitledBorder(borderForAddPanel, "Add", TitledBorder.LEFT,
                TitledBorder.BELOW_BOTTOM, new Font("Hack", Font.BOLD, 11), Color.DARK_GRAY);
        addTitleBorder.setTitleJustification(TitledBorder.LEFT);
        addTitleBorder.setTitlePosition(TitledBorder.DEFAULT_POSITION);
        addMemberPanel.setBorder(addTitleBorder);

        JPanel propertyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        checkStageQualify    = new JCheckBox(" stage");
        checkMicQualify      = new JCheckBox("<html>rotate<br/>mic</html>");
        checkHall2Qualify    = new JCheckBox("<html>Hall 2<br/>assistance<html>");
        checkSundayException = new JCheckBox("<html>Sunday stage<br/>exception</html>");

        propertyPanel.add(checkStageQualify);
        propertyPanel.add(checkMicQualify);
        propertyPanel.add(checkHall2Qualify);
        propertyPanel.add(checkSundayException);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addButton  = new JButton("Add");

        ActionListener addButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Member member = new Member();
                member.setFirstName(firstNameField.getText().equals("") ? null : firstNameField.getText());
                member.setLastName(lastNameField.getText().equals("") ? null : lastNameField.getText());
                member.setCanBeStage(checkStageQualify.isSelected());
                member.setCanRotateMic(checkMicQualify.isSelected());
                member.setCanAssist2ndHall(checkHall2Qualify.isSelected());
                member.setSundayException(checkSundayException.isSelected());
                if (member.save())
                    clearAddFields();
            }
        };
        addButton.addActionListener(addButtonListener);

        buttonPanel.add(addButton);

        Border borderForEditPanel    = new LineBorder(Color.LIGHT_GRAY, 1);
        TitledBorder editTitleBorder = new TitledBorder(borderForEditPanel, "Edit/Delete", TitledBorder.LEFT,
                TitledBorder.BELOW_BOTTOM, new Font("Hack", Font.BOLD, 11), Color.DARK_GRAY);
        editTitleBorder.setTitleJustification(TitledBorder.LEFT);
        editTitleBorder.setTitlePosition(TitledBorder.DEFAULT_POSITION);
        editDeleteMemberPanel.setBorder(editTitleBorder);

        JButton editButton   = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        editDeleteMemberPanel.add(editButton);
        editDeleteMemberPanel.add(deleteButton);

        addMemberPanel.add(namePanel);
        addMemberPanel.add(propertyPanel);
        addMemberPanel.add(buttonPanel);

        memberManagement.add(addMemberPanel);
        memberManagement.add(editDeleteMemberPanel);

        // **************************** Filling TabbedPane ****************************

        tabbedPane.add("Generate", generate);
        tabbedPane.add("Member", memberManagement);

        add(tabbedPane);

        pack();
        setLocation(622, 354);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void clearAddFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        checkStageQualify.setSelected(false);
        checkMicQualify.setSelected(false);
        checkHall2Qualify.setSelected(false);
        checkSundayException.setSelected(false);
    }

    public static void main(String[] args) {
        GeneratorGUI gui = new GeneratorGUI();
    }
}
