import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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
    private JButton addMemberButton;
    private JButton updateMemberButton;
    private JButton removeMemberButton;
    private JTable membersTable;
    private final DefaultTableModel tableModel;
    private JTabbedPane tabbedPane;
    private JScrollPane scrollPane;
    private JComboBox<String> monthComboBox;
    private String savePath = "/home/miki/Desktop/";
    private final HashMap<String, Integer> AMMonths;

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
              return column != 0;
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
                JFileChooser saveLocation = new JFileChooser();
                saveLocation.setDialogTitle("Excel ሰነዱ የት ይቀመጥ?");
                saveLocation.setCurrentDirectory(new File("/home/miki/Desktop/"));
                saveLocation.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                saveLocation.setMultiSelectionEnabled(false);
                saveLocation.setAcceptAllFileFilterUsed(false);

                int choice = saveLocation.showDialog(frame, "save");
                if (choice == JFileChooser.CANCEL_OPTION)
                    return;
                savePath = saveLocation.getSelectedFile().getPath();
                LocalDateTime date = LocalDateTime.of(
                        (int) yearSpinner.getValue(),
                        AMMonths.get(Objects.requireNonNull(monthComboBox.getSelectedItem()).toString()),
                        (int) daySpinner.getValue(), 0, 0
                );
                ExcelFileGenerator excelFileGenerator = new ExcelFileGenerator((int) howManyWeeksSpinner.getValue());
                //noinspection ConstantConditions
                if (excelFileGenerator.makeExcel (date , meetingDayMidweekComboBox.getSelectedItem().toString(), savePath)) {
                    JOptionPane.showMessageDialog(frame, "ፕሮግራሙ ተፈጥሯል", "ተሳክቷል", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                JOptionPane.showMessageDialog(frame, "ያልታወቀ ችግር ተፈጥሯል", "ስህተት", JOptionPane.ERROR_MESSAGE);

            }
        });

        tableModel.addColumn("#");
        tableModel.addColumn("ስም");
        tableModel.addColumn("መድረክ");
        tableModel.addColumn("ድምጽ ማጉያ");
        tableModel.addColumn("ሁለተኛው አዳራሽ");
        tableModel.addColumn("የእሁድ ልዩነት");
        membersTable.setModel(tableModel);

        membersTable.getColumnModel().getColumn(0).setMinWidth(50);
        membersTable.getColumnModel().getColumn(0).setMaxWidth(50);

        membersTable.getColumnModel().getColumn(2).setCellEditor(membersTable.getDefaultEditor(Boolean.class));
        membersTable.getColumnModel().getColumn(2).setCellRenderer(membersTable.getDefaultRenderer(Boolean.class));

        membersTable.getColumnModel().getColumn(3).setCellEditor(membersTable.getDefaultEditor(Boolean.class));
        membersTable.getColumnModel().getColumn(3).setCellRenderer(membersTable.getDefaultRenderer(Boolean.class));

        membersTable.getColumnModel().getColumn(4).setCellEditor(membersTable.getDefaultEditor(Boolean.class));
        membersTable.getColumnModel().getColumn(4).setCellRenderer(membersTable.getDefaultRenderer(Boolean.class));

        membersTable.getColumnModel().getColumn(5).setCellEditor(membersTable.getDefaultEditor(Boolean.class));
        membersTable.getColumnModel().getColumn(5).setCellRenderer(membersTable.getDefaultRenderer(Boolean.class));

        membersTable.getTableHeader().setReorderingAllowed(false);
        membersTable.getTableHeader().setResizingAllowed(true);

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

        membersTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        Dimension tableDimension = membersTable.getPreferredSize();
        scrollPane.setPreferredSize(new Dimension((int) tableDimension.getWidth(), (int) tableDimension.getHeight() + 40));

        daySpinner.setModel(new SpinnerNumberModel(1, 1, 31, 1));
        yearSpinner.setModel(new SpinnerNumberModel(2018, 2018, 3000, 1));
        howManyWeeksSpinner.setModel(new SpinnerNumberModel(1, 1, 100, 1));

        //noinspection Convert2Lambda
        addMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!validMemberInfo()) {
                    JOptionPane.showMessageDialog(frame, "የወንድም ሙሉ ስም አልተሞላም", "ስህተት", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Member member             = new Member();
                Object[] memberProperties = new Object[6];
                member.setFirstName(FirstNameTextField.getText());
                member.setLastName(lastNameTextField.getText());
                try {
                    List<Member> duplicateNamedMembers = Member.getDao().queryBuilder().where()
                            .eq("firstName", member.getFirstName()).query();
                    member.setHasDuplicateFirstName(duplicateNamedMembers.size() > 0);
                    for (Member duplicateNamedMember : duplicateNamedMembers) {
                        duplicateNamedMember.setHasDuplicateFirstName(true);
                        Member.getDao().update(duplicateNamedMember);
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
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
        removeMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selection = membersTable.getSelectedRow();
                if (selection == -1)
                    return;
                int option = JOptionPane.showConfirmDialog(frame, "እርግጠኛ ነህ?", "", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.NO_OPTION)
                    return;
                int selectedRow     = membersTable.getSelectedRow();
                String selectedName = membersTable.getValueAt(selectedRow, 1).toString();
                int indexOfSpace    = selectedName.indexOf(" ");
                if (Member.remove((int) membersTable.getValueAt(selectedRow, 0))) {
                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(
                            frame,
                            selectedName.substring(0, indexOfSpace) + " ወጥቷል...",
                            "", JOptionPane.INFORMATION_MESSAGE
                    );
                }
                /* once a member is removed from the database any other member with first name similar to
                   the member who just got removed must have his 'hasDuplicateFirstName' attribute updated */
                try {
                    List<Member> members = Member.getDao()
                            .queryBuilder().where().eq("firstName", selectedName.substring(0, indexOfSpace)).query();
                    for (Member member : members) {
                        member.setHasDuplicateFirstName(false);
                        Member.getDao().update(member);
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
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
                    List<Member> members = Member.getDao().queryBuilder().where()
                            .eq("firstName", member.getFirstName()).query();
                    member.setHasDuplicateFirstName(members.size() > 0);
                    Member.getDao().update(member);

                    for (Member _member : members) {
                        _member.setHasDuplicateFirstName(true);
                        Member.getDao().update(_member);
                    }
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
