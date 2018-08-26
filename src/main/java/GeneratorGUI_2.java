import com.j256.ormlite.stmt.DeleteBuilder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;

import javax.swing.*;

class GeneratorGUI_2 extends JFrame {
    private JSpinner daySpinner;
    private JSpinner monthSpinner;
    private JSpinner yearSpinner;
    private JComboBox meetingDayMidweekComboBox;
    private JSpinner howManyMonthsSpinner;
    private JButton generateButton;
    private JTextField FIrstNameTextField;
    private JTextField lastNameTextField;
    private JCheckBox stageCheckBox;
    private JCheckBox micCheckBox;
    private JCheckBox a2ndHallCheckBox;
    private JCheckBox sundayExceptionCheckBox;
    private JButton addButton;
    private JButton modifyButton;
    private JButton removeButton;
    private JTable table1;
    private JTabbedPane tabbedPane;
    private String savePath = "/home/miki/Desktop/";

    private void setupGUI() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem changeDirectoryItem = new JMenuItem("Save to");
        JMenuItem exitItem = new JMenuItem("Exit...");

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About...");

        changeDirectoryItem.addActionListener(e -> {
            JFileChooser excelLocation = new JFileChooser();
            excelLocation.setDialogTitle("Save excel file to...");
            excelLocation.setCurrentDirectory(new File("/home/miki/Desktop/"));
            excelLocation.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            excelLocation.setMultiSelectionEnabled(false);
            excelLocation.setAcceptAllFileFilterUsed(false);
            excelLocation.showDialog(null, "Select");
            savePath = excelLocation.getSelectedFile().getPath();

        });

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        fileMenu.add(changeDirectoryItem);
        fileMenu.add(exitItem);

        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        daySpinner.setModel(new SpinnerNumberModel(1, 1, 31, 1));
        monthSpinner.setModel(new SpinnerNumberModel(1, 1, 12, 1));
        yearSpinner.setModel(new SpinnerNumberModel(2018, 2018, 3000, 1));
        howManyMonthsSpinner.setModel(new SpinnerNumberModel(1, 1, 100, 1));

        addButton.addActionListener(e -> {
            if (!validMemberInfo()) return;
            Member member = new Member();
            member.setFirstName(FIrstNameTextField.getText());
            member.setLastName(lastNameTextField.getText());
            member.setCanBeStage(stageCheckBox.isSelected());
            member.setCanRotateMic(micCheckBox.isSelected());
            member.setCanAssist2ndHall(a2ndHallCheckBox.isSelected());
            member.setSundayException(sundayExceptionCheckBox.isSelected());
            if (member.save()) {
                JOptionPane.showMessageDialog(GeneratorGUI_2.getFrames()[0], "Member added!");
                clearAddFields();
            }
        });

        generateButton.addActionListener(e -> {
            ExcelFileGenerator excelFileGenerator = new ExcelFileGenerator((int) howManyMonthsSpinner.getValue());
            //noinspection ConstantConditions
            excelFileGenerator.makeExcel
            (
                    (int) yearSpinner.getValue(),
                    (int) monthSpinner.getValue(),
                    (int) daySpinner.getValue(),
                    meetingDayMidweekComboBox.getSelectedItem().toString(),
                    savePath
            );
        });

        removeButton.addActionListener(e -> {
            String fullName = JOptionPane.showInputDialog(null, "Enter full name: ", "Remove member", JOptionPane.PLAIN_MESSAGE);
            if (fullName == null || fullName.equals("")) return;
            String[] firstLastName = fullName.split(" ");
            DeleteBuilder<Member, Integer> deleter = Member.getDao().deleteBuilder();
            try {
                deleter.where().eq("firstName", firstLastName[0]);
//                    deleter.where().eq("lastName", firstLastName[1]);
                deleter.delete();
                JOptionPane.showMessageDialog(null, "Member removed");
            }
            catch (SQLException b) {
                b.printStackTrace();
            }
        });

        setContentPane(tabbedPane);
        pack();
        setResizable(false);
        setLocation(500, 250);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private boolean validMemberInfo() {
        /* as the input is likely to be constructed using Amharic letters,
         I see no way I can use regex to test for it's validity*/
        boolean firstName = !FIrstNameTextField.getText().equals("");
        boolean lastName  = !lastNameTextField.getText().equals("");
        return firstName && lastName;
    }

    private void clearAddFields() {
        FIrstNameTextField.setText("");
        lastNameTextField.setText("");
        stageCheckBox.setSelected(false);
        micCheckBox.setSelected(false);
        a2ndHallCheckBox.setSelected(false);
        sundayExceptionCheckBox.setSelected(false);
    }

    public static void main(String[] args) {
        GeneratorGUI_2 gui_2 = new GeneratorGUI_2();
        gui_2.setupGUI();
    }
}
