import java.awt.Dimension;
import java.io.File;
import java.sql.SQLException;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

class GeneratorGUI extends JFrame {
    private final JFrame frame = this;
    private JSpinner daySpinner;
    private JSpinner monthSpinner;
    private JSpinner yearSpinner;
    private JComboBox meetingDayMidweekComboBox;
    private JSpinner howManyWeeksSpinner;
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
    private JTable table;
    private DefaultTableModel tableModel;
    private JTabbedPane tabbedPane;
    private JScrollPane scrollPane;
    private String savePath = "/home/miki/Desktop/";

    private void setupGUI() {
        setTitle("Sound System Schedule Generator");
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
            
            int choice = excelLocation.showDialog(frame, "Select");
            if (choice == JFileChooser.CANCEL_OPTION)
                return;
            savePath = excelLocation.getSelectedFile().getPath();

        });

        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(changeDirectoryItem);
        fileMenu.add(exitItem);

        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        generateButton.addActionListener(e -> {
            ExcelFileGenerator excelFileGenerator = new ExcelFileGenerator((int) howManyWeeksSpinner.getValue());
            //noinspection ConstantConditions
            if (excelFileGenerator.makeExcel
                    (
                            (int) yearSpinner.getValue(),
                            (int) monthSpinner.getValue(),
                            (int) daySpinner.getValue(),
                            meetingDayMidweekComboBox.getSelectedItem().toString(),
                            savePath
                    )) {
                JOptionPane.showMessageDialog(frame, "Schedule generated", "Success", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(frame, "An unknown error has occurred", "Error", JOptionPane.ERROR_MESSAGE);

        });

        tableModel = new DefaultTableModel();
        tableModel.addColumn("id");
        tableModel.addColumn("ስም");
        tableModel.addColumn("መድረክ");
        tableModel.addColumn("ድምጽ ማጉያ");
        tableModel.addColumn("ሁለተኛው አዳራሽ");
        tableModel.addColumn("የእሁድ ልዩነት");
        table.setModel(tableModel);
        table.getTableHeader().setReorderingAllowed(false);

        try {
            List<Member> allMembers = Member.getDao().queryForAll();
            Object[] memberProperties = new Object[6];
            for (Member member : allMembers) {
                memberProperties[0] = member.getId();
                memberProperties[1] = member.getFirstName() + " " + member.getLastName();
                memberProperties[2] = member.canBeStage();
                memberProperties[3] = member.canRotateMic();
                memberProperties[4] = member.canBeSecondHall();
                memberProperties[5] = member.hasSundayException();
                tableModel.addRow(memberProperties);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        Dimension tableDimension = table.getPreferredSize();
        scrollPane.setPreferredSize(new Dimension((int) tableDimension.getWidth(), (int) tableDimension.getHeight()));

        daySpinner.setModel(new SpinnerNumberModel(1, 1, 31, 1));
        monthSpinner.setModel(new SpinnerNumberModel(1, 1, 12, 1));
        yearSpinner.setModel(new SpinnerNumberModel(2018, 2018, 3000, 1));
        howManyWeeksSpinner.setModel(new SpinnerNumberModel(1, 1, 100, 1));

        addButton.addActionListener(e -> {
            if (!validMemberInfo()) return;
            Member member = new Member();
            Object[] memberProperties = new Object[6];
            member.setFirstName(FIrstNameTextField.getText());
            member.setLastName(lastNameTextField.getText());
            member.setCanBeStage(stageCheckBox.isSelected());
            member.setCanRotateMic(micCheckBox.isSelected());
            member.setCanAssist2ndHall(a2ndHallCheckBox.isSelected());
            member.setSundayException(sundayExceptionCheckBox.isSelected());
            if (member.save()) {
                JOptionPane.showMessageDialog(GeneratorGUI.getFrames()[0], "Member added");
                clearAddFields();
            }
            memberProperties[0] = member.getId();
            memberProperties[1] = member.getFirstName() + " " + member.getLastName();
            memberProperties[2] = member.canBeStage();
            memberProperties[3] = member.canRotateMic();
            memberProperties[4] = member.canBeSecondHall();
            memberProperties[5] = member.hasSundayException();
            tableModel.addRow(memberProperties);
        });

        removeButton.addActionListener(e -> {
            int selection = table.getSelectedRow();
            if (selection == -1)
                return;
            int option = JOptionPane.showConfirmDialog(frame, "Are you sure?", "Remove member...", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.NO_OPTION)
                return;
            int selectedRow = table.getSelectedRow();
            if (Member.remove((int) table.getValueAt(selectedRow, 0)))
                JOptionPane.showMessageDialog(frame, "Member removed...", "", JOptionPane.INFORMATION_MESSAGE);
            tableModel.removeRow(selectedRow);
        });

        modifyButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1)
                return;
            Member member = new Member();
            String[] firstLastName = table.getValueAt(selectedRow, 1).toString().split(" ");
            member.setId(Integer.parseInt(table.getValueAt(selectedRow, 0).toString()));
            member.setFirstName(firstLastName[0]);
            member.setLastName(firstLastName[1]);
            member.setCanBeStage(Boolean.parseBoolean(table.getValueAt(selectedRow, 2).toString()));
            member.setCanRotateMic(Boolean.parseBoolean(table.getValueAt(selectedRow, 3).toString()));
            member.setCanAssist2ndHall(Boolean.parseBoolean(table.getValueAt(selectedRow, 4).toString()));
            member.setSundayException(Boolean.parseBoolean(table.getValueAt(selectedRow, 5).toString()));
            try {
                Member.getDao().update(member);
            } catch (SQLException e1) {
                System.out.println(e1.getMessage());
            }
            JOptionPane.showMessageDialog(frame, "Member's properties updated");
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
        GeneratorGUI gui_2 = new GeneratorGUI();
        gui_2.setupGUI();
    }
}
