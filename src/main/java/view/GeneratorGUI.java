package view;

import controller.ExcelFileGenerator;
import domain.Member;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class GeneratorGUI extends JFrame {
    private final JFrame frame = this;
    private JSpinner daySpinner;
    private JSpinner yearSpinner;
    private JComboBox<String> midweekMeetingDayComboBox;
    private JSpinner howManyWeeksSpinner;
    private JButton generateButton;
    private JTextField FirstNameTextField;
    private JTextField lastNameTextField;
    private JCheckBox stageCheckBox;
    private JCheckBox micCheckBox;
    private JCheckBox secondHallCheckBox;
    private JCheckBox sundayExceptionCheckBox;
    private JButton addMemberButton;
    private JButton updateMemberButton;
    private JButton removeMemberButton;
    private JTable membersTable;
    private final DefaultTableModel tableModel;
    private JTabbedPane tabbedPane;
    private JScrollPane scrollPane;
    private JComboBox<String> monthComboBox;
    private JComboBox<String> weekendMeetingDayComboBox;
    private JCheckBox otherSundayMeetingDayCheckbox;
    private JLabel informationLabel;
    private JLabel programStartDayLabel;
    private JLabel programStartMonthLabel;
    private JLabel programStartYearLabel;
    private JLabel serviceMeetingLabel;
    private JLabel sundayMeetingLabel;
    private JLabel howManyWeeksSpinnerLabel;
    private JPanel programTab;
    private JLabel firstNameFieldLabel;
    private JLabel lastNameFieldLabel;
    private JPanel tablePanel;
    private JPanel newMemberPanel;
    private final HashMap<String, Integer> AMMonths;
    private final int ID_COLUMN = 0, STAGE = 2, MIC = 3, HALL2 = 4, SUNDAY_EXCEPTION = 5;
    private List<Member> allMembers;

    {
        try {
            allMembers = Member.memberDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public GeneratorGUI() {
        tabbedPane.setTitleAt(0, MessagesAndTitles.PROGRAM_TAB_TITLE);
        tabbedPane.setTitleAt(1, MessagesAndTitles.MEMBER_TAB_TITLE);

        // program tab labels and titles
        programTab.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1), MessagesAndTitles.PROGRAM_TAB_TITLE)
        );
        informationLabel.setText(MessagesAndTitles.REQUIREMENT_DETAILS_LABEL);
        programStartDayLabel.setText(MessagesAndTitles.DAY_SPINNER_LABEL);
        programStartMonthLabel.setText(MessagesAndTitles.MONTH_DROPDOWN_LABEL);
        programStartYearLabel.setText(MessagesAndTitles.YEAR_SPINNER_LABEL);
        serviceMeetingLabel.setText(MessagesAndTitles.SERVICE_MEETING_DROPDOWN_LABEL);
        sundayMeetingLabel.setText(MessagesAndTitles.SUNDAY_MEETING_DROPDOWN_LABEL);
        otherSundayMeetingDayCheckbox.setText(MessagesAndTitles.OTHER_SUNDAY_MEETING_CHECKBOX_LABEL);
        howManyWeeksSpinnerLabel.setText(MessagesAndTitles.HOW_MANY_WEEKS_SPINNER_LABEL);
        // member tab labels and titles
        newMemberPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1), MessagesAndTitles.MEMBER_BORDER_TITLE)
        );
        firstNameFieldLabel.setText(MessagesAndTitles.FIRST_NAME_TEXTFIELD_LABEL);
        lastNameFieldLabel.setText(MessagesAndTitles.LAST_NAME_TEXTFIELD_LABEL);
        stageCheckBox.setText(MessagesAndTitles.STAGE_CHECKBOX_LABEL);
        micCheckBox.setText(MessagesAndTitles.MICROPHONE_CHECKBOX_LABEL);
        secondHallCheckBox.setText(MessagesAndTitles.SECOND_HALL_CHECKBOX_LABEL);
        sundayExceptionCheckBox.setText(MessagesAndTitles.SUNDAY_EXCEPTION_CHECKBOX_LABEL);

        stageCheckBox.setToolTipText(MessagesAndTitles.STAGE_CHECKBOX_TOOLTIP);
        micCheckBox.setToolTipText(MessagesAndTitles.MIC_CHECKBOX_TOOLTIP);
        secondHallCheckBox.setToolTipText(MessagesAndTitles.SECOND_HALL_CHECKBOX_TOOLTIP);
        sundayExceptionCheckBox.setToolTipText(MessagesAndTitles.SUNDAY_EXCEPTION_CHECKBOX_TOOLTIP);

        addMemberButton.setText(MessagesAndTitles.ADD_BUTTON_TEXT);
        updateMemberButton.setText(MessagesAndTitles.UPDATE_BUTTON_TITLE);
        removeMemberButton.setText(MessagesAndTitles.REMOVE_BUTTON_TITLE);

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

    public void setupAndDrawUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            System.out.println(e.getMessage());
        }
        setTitle(MessagesAndTitles.GENERATOR_FRAME_TITLE);

        JMenuBar menuBar = new JMenuBar();

        PreferenceForm preferenceForm = new PreferenceForm(this);
        AboutForm aboutForm = new AboutForm(this);

        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit...");
        JMenuItem preferencesItem = new JMenuItem("Preferences...");

        JMenu aboutMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About...");

        //noinspection Convert2Lambda
        preferencesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // disables itself
                setEnabled(false);
                preferenceForm.setLocationRelativeTo(frame);
                preferenceForm.setVisible(true);
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
                // disables itself
                setEnabled(false);
                aboutForm.getFrame().setLocationRelativeTo(frame);
                aboutForm.getFrame().setVisible(true);
            }
        });

        fileMenu.add(preferencesItem);
        fileMenu.add(exitItem);

        aboutMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(aboutMenu);

        setJMenuBar(menuBar);

        tablePanel.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.GRAY, 1), MessagesAndTitles.MEMBER_TABLE_BORDER_TITLE
                )
        );
        tableModel.addColumn(MessagesAndTitles.ID_COLUMN);
        tableModel.addColumn(MessagesAndTitles.FULL_NAME_COLUMN);
        tableModel.addColumn(MessagesAndTitles.STAGE_COLUMN);
        tableModel.addColumn(MessagesAndTitles.MIC_COLUMN);
        tableModel.addColumn(MessagesAndTitles.HALL2_COLUMN);
        tableModel.addColumn(MessagesAndTitles.SUNDAY_EXCEPTION_COLUMN);
        membersTable.setModel(tableModel);

        membersTable.getColumnModel().getColumn(ID_COLUMN).setMinWidth(50);
        membersTable.getColumnModel().getColumn(ID_COLUMN).setMaxWidth(50);

        renderTableColumnAsCheckbox(STAGE);
        renderTableColumnAsCheckbox(MIC);
        renderTableColumnAsCheckbox(HALL2);
        renderTableColumnAsCheckbox(SUNDAY_EXCEPTION);

        membersTable.getTableHeader().setReorderingAllowed(false);
        membersTable.getTableHeader().setResizingAllowed(true);

        for (Member member : allMembers) {
            addTableRowForMember(member);
        }

        membersTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        Dimension tableDimension = membersTable.getPreferredSize();
        scrollPane.setPreferredSize(new Dimension((int) tableDimension.getWidth(), 190));

        daySpinner.setModel(new SpinnerNumberModel(1, 1, 31, 1));
        yearSpinner.setModel(new SpinnerNumberModel(2018, 2018, 3000, 1));
        // the maximum number of weeks for which the generated table won't exceed an A4 paper is 22
        howManyWeeksSpinner.setModel(new SpinnerNumberModel(22, 1, 100, 1));

        monthComboBox.setModel(new DefaultComboBoxModel<>(MessagesAndTitles.MONTHS));
        midweekMeetingDayComboBox.setModel(new DefaultComboBoxModel<>(MessagesAndTitles.MIDWEEK_MEETING_DAYS));
        weekendMeetingDayComboBox.setModel(new DefaultComboBoxModel<>(MessagesAndTitles.WEEKEND_MEETING_DAYS));

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

        generateButton.setText(MessagesAndTitles.GENERATE_BUTTON_TEXT);
        //noinspection Convert2Lambda
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UIController uiController = new UIController();
                uiController.execute();
            }
        });

        //noinspection Convert2Lambda
        addMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (invalidMemberInfo()) {
                    JOptionPane.showMessageDialog(frame, MessagesAndTitles.INCOMPLETE_MEMBER_NAME_MESSAGE,
                            MessagesAndTitles.ERROR_DIALOGUE_TITLE, JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Member member = new Member();
                member.setFirstName(FirstNameTextField.getText());
                member.setLastName(lastNameTextField.getText());
                member.setCanBeStage(stageCheckBox.isSelected());
                member.setCanRotateMic(micCheckBox.isSelected());
                member.setCanAssist2ndHall(secondHallCheckBox.isSelected());
                member.setSundayException(sundayExceptionCheckBox.isSelected());
                if (member.save()) {
                    JOptionPane.showMessageDialog(GeneratorGUI.getFrames()[0], "\"" + member.getFirstName() + "\" added");
                    GeneratorGUI.this.clearInputFields();
                }
                addTableRowForMember(member);
                // handling duplicateFirstName attribute issue(s)
                try {
                    updateDuplicateAttributeOnAddition(member);
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

                Member member = new Member();
                String[] firstLastName = membersTable.getValueAt(selectedRow, 1).toString().split(" ");
                member.setId(Integer.parseInt(membersTable.getValueAt(selectedRow, 0).toString()));
                member.setFirstName(firstLastName[0]);
                member.setLastName(firstLastName[1]);
                member.setCanBeStage((boolean) membersTable.getValueAt(selectedRow, 2));
                member.setCanRotateMic((boolean) membersTable.getValueAt(selectedRow, 3));
                member.setCanAssist2ndHall((boolean) membersTable.getValueAt(selectedRow, 4));
                member.setSundayException((boolean) membersTable.getValueAt(selectedRow, 5));
                try {
                    String oldName = Member.memberDao.queryForId(member.getId()).getFirstName();
                    Member.memberDao.update(member);
                    updateMemberDuplicateAttributeOnUpdate(member, oldName);
                } catch (SQLException e1) {
                    System.out.println(e1.getMessage());
                }
                JOptionPane.showMessageDialog(frame, member.getFirstName() + "'s attributes updated");
            }
        });

        //noinspection Convert2Lambda
        removeMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = membersTable.getSelectedRow();
                if (selectedRow == -1) return;

                int option = JOptionPane.showConfirmDialog(frame, "Are you sure?", "", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.NO_OPTION) return;

                int memberID = (int) membersTable.getValueAt(selectedRow, 0);
                Member member = new Member();
                try {
                    member = Member.memberDao.queryForId(memberID);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                if (Member.remove(memberID)) {
                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(
                            frame,
                            "\"" + member.getFirstName() + "\" removed...",
                            "",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }

                try {
                    updateDuplicateAttributeOnDelete(member);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
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
        // As the input is likely to be constructed using Amharic letters,
        // I see no way I can use regex to test for it's validity
        boolean invalidFirstName = FirstNameTextField.getText().isEmpty();
        boolean invalidLastName = lastNameTextField.getText().isEmpty();
        return invalidFirstName || invalidLastName;
    }

    private void updateDuplicateAttributeOnAddition(Member member) throws SQLException {
        List<Member> members = Member.memberDao.queryBuilder().where()
                .eq("firstName", member.getFirstName()).query();
        if (members.size() > 1) {
            for (Member _member : members) {
                _member.setHasDuplicateFirstName(true);
                Member.memberDao.update(_member);
            }
        }
    }

    private void updateMemberDuplicateAttributeOnUpdate(Member member, String oldName) throws SQLException {
        updateDuplicateAttributeOnAddition(member);
        member.setFirstName(oldName);
        updateDuplicateAttributeOnDelete(member);
    }

    private void updateDuplicateAttributeOnDelete(Member member) throws SQLException {
        List<Member> members = Member.memberDao.queryBuilder().where()
                .eq("firstName", member.getFirstName()).query();
        // if this condition is met, then the returned list will have only one member
        if (members.size() <= 1) {
            for (Member _member : members) {
                _member.setHasDuplicateFirstName(false);
                Member.memberDao.update(_member);
            }
        }
    }

    private void renderTableColumnAsCheckbox(int columnIndex) {
        membersTable.getColumnModel().getColumn(columnIndex).setCellEditor
                (membersTable.getDefaultEditor(Boolean.class));
        membersTable.getColumnModel().getColumn(columnIndex).setCellRenderer
                (membersTable.getDefaultRenderer(Boolean.class));
    }

    private void addTableRowForMember(Member member) {
        final int FULL_NAME = 1;
        Object[] memberAttributes = new Object[6];

        memberAttributes[ID_COLUMN] = member.getId();
        memberAttributes[FULL_NAME] = member.getFirstName() + " " + member.getLastName();
        memberAttributes[STAGE] = member.canBeStage();
        memberAttributes[MIC] = member.canRotateMic();
        memberAttributes[HALL2] = member.canBe2ndHall();
        memberAttributes[SUNDAY_EXCEPTION] = member.hasSundayException();
        tableModel.addRow(memberAttributes);
    }

    private void clearInputFields() {
        FirstNameTextField.setText("");
        lastNameTextField.setText("");
        stageCheckBox.setSelected(false);
        micCheckBox.setSelected(false);
        secondHallCheckBox.setSelected(false);
        sundayExceptionCheckBox.setSelected(false);
    }

    private class UIController extends SwingWorker<Void, Void> {
        private int RETURN_STATUS;

        @Override
        protected Void doInBackground() {
            JFileChooser saveLocation = new JFileChooser();
            saveLocation.setDialogType(JFileChooser.SAVE_DIALOG);
            saveLocation.setDialogTitle("Where to save the Excel document?");
            saveLocation.setCurrentDirectory(new File(System.getProperty("user.home")));
            saveLocation.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            saveLocation.setMultiSelectionEnabled(false);
            saveLocation.setAcceptAllFileFilterUsed(false);

            int choice = saveLocation.showSaveDialog(frame);
            if (choice == JFileChooser.CANCEL_OPTION) {
                RETURN_STATUS = -1;
                return null;
            }

            generateButton.setEnabled(false);

            LocalDateTime beginDate = LocalDateTime.of(
                    (int) yearSpinner.getValue(),
                    AMMonths.get(Objects.requireNonNull(monthComboBox.getSelectedItem()).toString()),
                    (int) daySpinner.getValue(), 0, 0
            );
            ExcelFileGenerator excelFileGenerator = new ExcelFileGenerator
                    ((int) howManyWeeksSpinner.getValue(), beginDate);
            String savePath = saveLocation.getSelectedFile().getPath();
            // noinspection ConstantConditions
            RETURN_STATUS = excelFileGenerator.makeExcel(
                    midweekMeetingDayComboBox.getSelectedItem().toString(),
                    weekendMeetingDayComboBox.getSelectedItem().toString(),
                    allMembers,
                    savePath
            );

            return null;
        }

        @Override
        protected void done() {
            generateButton.setEnabled(true);

            switch (RETURN_STATUS) {
                case -1: // user canceled operation
                    JOptionPane.showMessageDialog(frame, MessagesAndTitles.USER_CANCELED_OPERATION_MESSAGE,
                            "", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case 0: // success
                    JOptionPane.showMessageDialog(frame, MessagesAndTitles.SCHEDULE_CREATED_MESSAGE,
                            MessagesAndTitles.SUCCESS_DIALOGUE_TITLE, JOptionPane.INFORMATION_MESSAGE);
                    break;
                case 1: // could not save file
                    JOptionPane.showMessageDialog(frame, MessagesAndTitles.COULD_NOT_SAVE_FILE_MESSAGE,
                            MessagesAndTitles.ERROR_DIALOGUE_TITLE, JOptionPane.ERROR_MESSAGE);
                    break;
                case 2: // database has 0 records (empty array)
                    JOptionPane.showMessageDialog(frame, MessagesAndTitles.NO_MEMBERS_FOUND_MESSAGE,
                            MessagesAndTitles.ERROR_DIALOGUE_TITLE, JOptionPane.ERROR_MESSAGE);
                    break;
                default: // unknown problem
                    JOptionPane.showMessageDialog(frame, MessagesAndTitles.UNKNOWN_PROBLEM_MESSAGE,
                            MessagesAndTitles.ERROR_DIALOGUE_TITLE, JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
    }
}
