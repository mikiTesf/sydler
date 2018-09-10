import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

class GeneratorGUI extends JFrame {
    private final JFrame frame = this;
    private JSpinner daySpinner;
    private JSpinner yearSpinner;
    private JComboBox meetingDayMidweekComboBox;
    private JSpinner howManyWeeksSpinner;
    private JButton generateButton;
    private JTextField FirstNameTextField;
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
    private JComboBox<String> monthComboBox;
    private String savePath = "/home/miki/Desktop/";
    private HashMap<String, Integer> AMMonths;

    private GeneratorGUI() {
        AMMonths = new HashMap<>(12);
        AMMonths.put("ጥር", 1);
        AMMonths.put("የካቲት", 2);
        AMMonths.put("መጋቢት", 3);
        AMMonths.put("ሚያዝያ", 4);
        AMMonths.put("ግንቦት", 5);
        AMMonths.put("ሰኔ", 6);
        AMMonths.put("ሐምሌ", 7);
        AMMonths.put("ነሐሴ", 8);
        AMMonths.put("መስከረም", 9);
        AMMonths.put("ጥቅምት", 10);
        AMMonths.put("ህዳር", 11);
        AMMonths.put("ታህሳሥ", 12);
    }

    private void setupGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            System.out.println(e.getMessage());
        }
        setTitle("የድምጽ ክፍል ፕሮግራም አመንጪ");

        JMenuBar menuBar    = new JMenuBar();
        AboutForm aboutForm = new AboutForm();

        JMenu fileMenu      = new JMenu("File");
        JMenuItem exitItem  = new JMenuItem("Exit...");

        JMenu aboutMenu     = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About...");

        //noinspection Convert2Lambda
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        //noinspection Convert2Lambda
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aboutForm.getFrame().setLocation(frame.getLocationOnScreen().x + frame.getWidth() / 4,
                                                 frame.getLocationOnScreen().y / 2 + frame.getHeight() / 2);
                aboutForm.getFrame().setVisible(true);
            }
        });

        fileMenu.add(exitItem);

        aboutMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(aboutMenu);

        setJMenuBar(menuBar);

        //noinspection Convert2Lambda
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser excelLocation = new JFileChooser();
                excelLocation.setDialogTitle("Excel ፋይሉ የት ይቀመጥ?");
                excelLocation.setCurrentDirectory(new File("/home/miki/Desktop/"));
                excelLocation.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                excelLocation.setMultiSelectionEnabled(false);
                excelLocation.setAcceptAllFileFilterUsed(false);

                int choice = excelLocation.showDialog(frame, "Select");
                if (choice == JFileChooser.CANCEL_OPTION)
                    return;
                savePath = excelLocation.getSelectedFile().getPath();
                ExcelFileGenerator excelFileGenerator = new ExcelFileGenerator((int) howManyWeeksSpinner.getValue());
                //noinspection ConstantConditions
                if (excelFileGenerator.makeExcel (
                                (int) yearSpinner.getValue(),
                                AMMonths.get(monthComboBox.getSelectedItem().toString()),
                                (int) daySpinner.getValue(),
                                meetingDayMidweekComboBox.getSelectedItem().toString(),
                                savePath
                        )
                ) {
                    JOptionPane.showMessageDialog(frame, "ፕሮግራሙ ተፈጥሯል", "ተሳክቷል", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                JOptionPane.showMessageDialog(frame, "ያልታወቀ ችግር ተፈጥሯል", "ስህተት", JOptionPane.ERROR_MESSAGE);

            }
        });

        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("ስም");
        tableModel.addColumn("መድረክ");
        tableModel.addColumn("ድምጽ ማጉያ");
        tableModel.addColumn("ሁለተኛው አዳራሽ");
        tableModel.addColumn("የእሁድ ልዩነት");
        table.setModel(tableModel);

        table.getColumnModel().getColumn(2).setCellEditor(table.getDefaultEditor(Boolean.class));
        table.getColumnModel().getColumn(2).setCellRenderer(table.getDefaultRenderer(Boolean.class));

        table.getColumnModel().getColumn(3).setCellEditor(table.getDefaultEditor(Boolean.class));
        table.getColumnModel().getColumn(3).setCellRenderer(table.getDefaultRenderer(Boolean.class));

        table.getColumnModel().getColumn(4).setCellEditor(table.getDefaultEditor(Boolean.class));
        table.getColumnModel().getColumn(4).setCellRenderer(table.getDefaultRenderer(Boolean.class));

        table.getColumnModel().getColumn(5).setCellEditor(table.getDefaultEditor(Boolean.class));
        table.getColumnModel().getColumn(5).setCellRenderer(table.getDefaultRenderer(Boolean.class));

        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(true);

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
        yearSpinner.setModel(new SpinnerNumberModel(2018, 2018, 3000, 1));
        howManyWeeksSpinner.setModel(new SpinnerNumberModel(1, 1, 100, 1));

        //noinspection Convert2Lambda
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!GeneratorGUI.this.validMemberInfo())
                    return;
                Member member             = new Member();
                Object[] memberProperties = new Object[6];
                member.setFirstName(FirstNameTextField.getText());
                member.setLastName(lastNameTextField.getText());
                member.setCanBeStage(stageCheckBox.isSelected());
                member.setCanRotateMic(micCheckBox.isSelected());
                member.setCanAssist2ndHall(a2ndHallCheckBox.isSelected());
                member.setSundayException(sundayExceptionCheckBox.isSelected());
                if (member.save()) {
                    JOptionPane.showMessageDialog(GeneratorGUI.getFrames()[0], member.getFirstName() + " ተጨምሯል");
                    GeneratorGUI.this.clearAddFields();
                }
                memberProperties[0] = member.getId();
                memberProperties[1] = member.getFirstName() + " " + member.getLastName();
                memberProperties[2] = member.canBeStage();
                memberProperties[3] = member.canRotateMic();
                memberProperties[4] = member.canBeSecondHall();
                memberProperties[5] = member.hasSundayException();
                tableModel.addRow(memberProperties);
            }
        });

        //noinspection Convert2Lambda
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selection = table.getSelectedRow();
                if (selection == -1)
                    return;
                int option = JOptionPane.showConfirmDialog(frame, "እርግጠኛ ነህ?", "", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.NO_OPTION)
                    return;
                int selectedRow     = table.getSelectedRow();
                String selectedName = table.getValueAt(selectedRow, 1).toString();
                int indexOfSpace    = selectedName.indexOf(" ");
                if (Member.remove((int) table.getValueAt(selectedRow, 0))) {
                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(frame, selectedName.substring(0, indexOfSpace) + " ወጥቷል...", "", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        //noinspection Convert2Lambda
        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1)
                    return;
                Member member          = new Member();
                String[] firstLastName = table.getValueAt(selectedRow, 1).toString().split(" ");
                member.setId(Integer.parseInt(table.getValueAt(selectedRow, 0).toString()));
                member.setFirstName(firstLastName[0]);
                member.setLastName(firstLastName[1]);
                member.setCanBeStage((boolean) table.getValueAt(selectedRow, 2));
                member.setCanRotateMic((boolean) table.getValueAt(selectedRow, 3));
                member.setCanAssist2ndHall((boolean) table.getValueAt(selectedRow, 4));
                member.setSundayException((boolean) table.getValueAt(selectedRow, 5));
                try {
                    Member.getDao().update(member);
                } catch (SQLException e1) {
                    System.out.println(e1.getMessage());
                }
                JOptionPane.showMessageDialog(frame, "የ" + member.getFirstName() + " አይነታዎች ተዘምነዋል");
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
        boolean validFirstName = !FirstNameTextField.getText().equals("");
        boolean validLastName  = !lastNameTextField.getText().equals("");
        return validFirstName && validLastName;
    }

    private void clearAddFields() {
        FirstNameTextField.setText("");
        lastNameTextField.setText("");
        stageCheckBox.setSelected(false);
        micCheckBox.setSelected(false);
        a2ndHallCheckBox.setSelected(false);
        sundayExceptionCheckBox.setSelected(false);
    }


    public static void main(String[] args) {
        GeneratorGUI gui = new GeneratorGUI();
        gui.setupGUI();
    }
}
