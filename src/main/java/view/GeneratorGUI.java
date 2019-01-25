package view;

import controller.ExcelFileGenerator;
import controller.SettingInitializer;
import domain.Member;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

class GeneratorGUI extends JFrame {
    private final JFrame frame = this;
    private JSpinner daySpinner;
    private JSpinner yearSpinner;
    private JComboBox midweekMeetingDayComboBox;
    private JSpinner howManyWeeksSpinner;
    private JButton generateButton;
    private JTextField FirstNameTextField;
    private JTextField lastNameTextField;
    private JCheckBox stageCheckBox;
    private JCheckBox micCheckBox;
    private JCheckBox a2ndHallCheckBox;
    private JCheckBox sundayExceptionCheckBox;
    private JButton addMemberButton;
    private JButton updateMemberButton;
    private JButton removeMemberButton;
    private JTable membersTable;
    private final DefaultTableModel tableModel;
    private JTabbedPane tabbedPane;
    private JScrollPane scrollPane;
    private JComboBox<String> monthComboBox;
    private JComboBox weekendMeetingDayComboBox;
    private JCheckBox otherSundayMeetingDayCheckbox;
    private String savePath = "/home/miki/Desktop/";
    private final HashMap<String, Integer> AMMonths;
    private final int ID_COLUMN = 0;

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

        tableModel = new DefaultTableModel() {
          @Override
          public boolean isCellEditable(int row, int column) {
              return column != ID_COLUMN;
          }
        };

    }

    private void setupGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            System.out.println(e.getMessage());
        }
        setTitle("የድምጽ ክፍል ፕሮግራም አመንጪ");

        JMenuBar menuBar    = new JMenuBar();

        GeneratorSettings settings = new GeneratorSettings(this);
        AboutForm aboutForm = new AboutForm(this);

        JMenu fileMenu            = new JMenu("File");
        JMenuItem exitItem        = new JMenuItem("Exit...");
        JMenuItem preferencesItem = new JMenuItem("Preferences...");

        JMenu aboutMenu     = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About...");

        //noinspection Convert2Lambda
        preferencesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setEnabled(false);
                settings.setLocationRelativeTo(frame);
                settings.setVisible(true);
            }
        });

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
                aboutForm.getFrame().setLocationRelativeTo(frame);
                setEnabled(false);
                aboutForm.getFrame().setVisible(true);
            }
        });

        fileMenu.add(preferencesItem);
        fileMenu.add(exitItem);

        aboutMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(aboutMenu);

        setJMenuBar(menuBar);

        //noinspection Convert2Lambda
        otherSundayMeetingDayCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                weekendMeetingDayComboBox.setEnabled(otherSundayMeetingDayCheckbox.isSelected());
                if (!otherSundayMeetingDayCheckbox.isSelected()) {
                    weekendMeetingDayComboBox.setSelectedIndex(0);
                }
            }
        });

        //noinspection Convert2Lambda
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser saveLocation = new JFileChooser();
                saveLocation.setDialogType(JFileChooser.SAVE_DIALOG);
                saveLocation.setDialogTitle("Excel ሰነዱ የት ይቀመጥ?");
                saveLocation.setCurrentDirectory(new File(System.getProperty("user.home")));
                saveLocation.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                saveLocation.setMultiSelectionEnabled(false);
                saveLocation.setAcceptAllFileFilterUsed(false);

                int choice = saveLocation.showDialog(frame, "save");
                if (choice == JFileChooser.CANCEL_OPTION) return;

                generateButton.setEnabled(false);

                LocalDateTime beginDate = LocalDateTime.of(
                        (int) yearSpinner.getValue(),
                        AMMonths.get(Objects.requireNonNull(monthComboBox.getSelectedItem()).toString()),
                        (int) daySpinner.getValue(), 0, 0
                );
                ExcelFileGenerator excelFileGenerator = new ExcelFileGenerator((int) howManyWeeksSpinner.getValue());
                savePath = saveLocation.getSelectedFile().getPath();
                //noinspection ConstantConditions
                final int RETURN_STATUS = excelFileGenerator.makeExcel(
                        beginDate,
                        midweekMeetingDayComboBox.getSelectedItem().toString(),
                        weekendMeetingDayComboBox.getSelectedItem().toString(),
                        savePath
                );
                generateButton.setEnabled(true);

                switch (RETURN_STATUS) {
                    case 0: // success status
                        JOptionPane.showMessageDialog(frame, "ፕሮግራሙ ተፈጥሯል", "ተሳክቷል", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    case 1:// could not save
                        JOptionPane.showMessageDialog(frame, "ሰነዱን ማስቀመጥ አልተቻለም", "ስህተት", JOptionPane.ERROR_MESSAGE);
                        break;
                    case 2:// empty array
                        JOptionPane.showMessageDialog(frame, "ምንም የድምጽ ክፍል አባላት አልተገኙም", "ስህተት", JOptionPane.ERROR_MESSAGE);
                        break;
                    default:// unknown problem
                        JOptionPane.showMessageDialog(frame, "ያልታወቀ ችግር ተፈጥሯል", "ስህተት", JOptionPane.ERROR_MESSAGE);
                        break;
                }
            }
        });

        tableModel.addColumn("#");
        tableModel.addColumn("ስም");
        tableModel.addColumn("መድረክ");
        tableModel.addColumn("ድምጽ ማጉያ");
        tableModel.addColumn("ሁለተኛው አዳራሽ");
        tableModel.addColumn("የእሁድ ልዩነት");
        membersTable.setModel(tableModel);

        final int FULL_NAME = 1, STAGE = 2, MIC = 3, HALL2 = 4, SUNDAY_EXCEPTION = 5;

        membersTable.getColumnModel().getColumn(ID_COLUMN).setMinWidth(50);
        membersTable.getColumnModel().getColumn(ID_COLUMN).setMaxWidth(50);

        membersTable.getColumnModel().getColumn(STAGE).setCellEditor(membersTable.getDefaultEditor(Boolean.class));
        membersTable.getColumnModel().getColumn(STAGE).setCellRenderer(membersTable.getDefaultRenderer(Boolean.class));

        membersTable.getColumnModel().getColumn(MIC).setCellEditor(membersTable.getDefaultEditor(Boolean.class));
        membersTable.getColumnModel().getColumn(MIC).setCellRenderer(membersTable.getDefaultRenderer(Boolean.class));

        membersTable.getColumnModel().getColumn(HALL2).setCellEditor(membersTable.getDefaultEditor(Boolean.class));
        membersTable.getColumnModel().getColumn(HALL2).setCellRenderer(membersTable.getDefaultRenderer(Boolean.class));

        membersTable.getColumnModel().getColumn(SUNDAY_EXCEPTION).setCellEditor(membersTable.getDefaultEditor(Boolean.class));
        membersTable.getColumnModel().getColumn(SUNDAY_EXCEPTION).setCellRenderer(membersTable.getDefaultRenderer(Boolean.class));

        membersTable.getTableHeader().setReorderingAllowed(false);
        membersTable.getTableHeader().setResizingAllowed(true);

        try {
            List<Member> allMembers = Member.getDao().queryForAll();
            Object[] memberProperties = new Object[6];
            for (Member member : allMembers) {
                memberProperties[ID_COLUMN]        = member.getId();
                memberProperties[FULL_NAME]        = member.getFirstName() + " " + member.getLastName();
                memberProperties[STAGE]            = member.canBeStage();
                memberProperties[MIC]              = member.canRotateMic();
                memberProperties[HALL2]            = member.canBe2ndHall();
                memberProperties[SUNDAY_EXCEPTION] = member.hasSundayException();
                tableModel.addRow(memberProperties);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        membersTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        Dimension tableDimension = membersTable.getPreferredSize();
        scrollPane.setPreferredSize(new Dimension((int) tableDimension.getWidth(), 190));

        daySpinner.setModel(new SpinnerNumberModel(1, 1, 31, 1));
        yearSpinner.setModel(new SpinnerNumberModel(2018, 2018, 3000, 1));
        // the maximum number of weeks for which the generated table won't exceed an A4 paper is 22
        howManyWeeksSpinner.setModel(new SpinnerNumberModel(22, 1, 100, 1));

        //noinspection Convert2Lambda
        addMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (invalidMemberInfo()) {
                    JOptionPane.showMessageDialog(frame, "የወንድም ሙሉ ስም አልተሞላም", "ስህተት", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Member member             = new Member();
                Object[] memberProperties = new Object[6];
                member.setFirstName(FirstNameTextField.getText());
                member.setLastName(lastNameTextField.getText());
                member.setCanBeStage(stageCheckBox.isSelected());
                member.setCanRotateMic(micCheckBox.isSelected());
                member.setCanAssist2ndHall(a2ndHallCheckBox.isSelected());
                member.setSundayException(sundayExceptionCheckBox.isSelected());
                if (member.save()) {
                    JOptionPane.showMessageDialog(GeneratorGUI.getFrames()[0], "\"" + member.getFirstName() + "\" ተጨምሯል");
                    GeneratorGUI.this.clearAddFields();
                }
                memberProperties[ID_COLUMN]        = member.getId();
                memberProperties[FULL_NAME]        = member.getFirstName() + " " + member.getLastName();
                memberProperties[STAGE]            = member.canBeStage();
                memberProperties[MIC]              = member.canRotateMic();
                memberProperties[HALL2]            = member.canBe2ndHall();
                memberProperties[SUNDAY_EXCEPTION] = member.hasSundayException();
                tableModel.addRow(memberProperties);
                // handling duplicateFirstName attribute issue(s)
                try {
                    updateDuplicateAttributeOnAddition(member);
                } catch (SQLException e1) { e1.printStackTrace(); }
            }
        });

        //noinspection Convert2Lambda
        updateMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = membersTable.getSelectedRow();
                if (selectedRow == -1)
                    return;

                Member member          = new Member();
                String[] firstLastName = membersTable.getValueAt(selectedRow, 1).toString().split(" ");
                member.setId(Integer.parseInt(membersTable.getValueAt(selectedRow, 0).toString()));
                member.setFirstName(firstLastName[0]);
                member.setLastName(firstLastName[1]);
                member.setCanBeStage((boolean) membersTable.getValueAt(selectedRow, 2));
                member.setCanRotateMic((boolean) membersTable.getValueAt(selectedRow, 3));
                member.setCanAssist2ndHall((boolean) membersTable.getValueAt(selectedRow, 4));
                member.setSundayException((boolean) membersTable.getValueAt(selectedRow, 5));
                try {
                    String oldName = Member.getDao().queryForId(member.getId()).getFirstName();
                    Member.getDao().update(member);
                    updateMemberDuplicateAttributeOnUpdate(member, oldName);
                } catch (SQLException e1) {
                    System.out.println(e1.getMessage());
                }
                JOptionPane.showMessageDialog(frame, "የ \"" + member.getFirstName() + "\" አይነታዎች ተዘምነዋል");
            }
        });

        //noinspection Convert2Lambda
        removeMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = membersTable.getSelectedRow();
                if (selectedRow == -1)  return;

                int option = JOptionPane.showConfirmDialog(frame, "እርግጠኛ ነህ?", "", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.NO_OPTION) return;

                int memberID = (int) membersTable.getValueAt(selectedRow, 0);
                Member member = new Member();
                try {
                    member = Member.getDao().queryForId(memberID);
                } catch (SQLException e1) { e1.printStackTrace(); }
                if (Member.remove(memberID)) {
                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(
                            frame,
                            "\"" + member.getFirstName() + "\" ወጥቷል...",
                            "",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }

                try {
                    updateDuplicateAttributeOnDelete(member);
                } catch (SQLException e1) { e1.printStackTrace(); }
            }
        });

        setContentPane(tabbedPane);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private boolean invalidMemberInfo() {
        /* as the input is likely to be constructed using Amharic letters,
         I see no way I can use regex to test for it's validity */
        boolean invalidFirstName = FirstNameTextField.getText().isEmpty();
        boolean invalidLastName  = lastNameTextField.getText().isEmpty();
        return invalidFirstName || invalidLastName;
    }

    private void updateDuplicateAttributeOnAddition(Member member) throws SQLException {
        List<Member> members = Member.getDao().queryBuilder().where()
                .eq("firstName", member.getFirstName()).query();
        if (members.size() > 1) {
            for (Member _member : members) {
                _member.setHasDuplicateFirstName(true);
                Member.getDao().update(_member);
            }
        }
    }

    private void updateMemberDuplicateAttributeOnUpdate (Member member, String oldName) throws SQLException {
        updateDuplicateAttributeOnAddition(member);
        member.setFirstName(oldName);
        updateDuplicateAttributeOnDelete(member);
    }

    private void updateDuplicateAttributeOnDelete(Member member) throws SQLException {
        List<Member> members = Member.getDao().queryBuilder().where()
                .eq("firstName", member.getFirstName()).query();
        // if this condition is met, then the returned list will have only one member
        if (members.size() <= 1) {
            for (Member _member : members) {
                _member.setHasDuplicateFirstName(false);
                Member.getDao().update(_member);
            }
        }
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
        SettingInitializer.initialize();
        System.setProperty("com.j256.ormlite.logger.level", "INFO");
        new GeneratorGUI().setupGUI();
    }
}
