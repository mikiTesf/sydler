package view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import controller.ExcelFileGenerator;
import domain.Member;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;

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
    private JPanel memberTab;
    private final HashMap<String, Integer> AMMonths;
    private final int ID_COLUMN = 0, STAGE = 2, MIC = 3, HALL2 = 4, SUNDAY_EXCEPTION = 5;
    private List<Member> allMembers;
    private final Properties UITexts = new Properties();

    {
        try {
            UITexts.load(getClass().getResourceAsStream("/UITexts.properties"));
            allMembers = Member.memberDao.queryForAll();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public GeneratorGUI() {
        tabbedPane.setTitleAt(0, UITexts.getProperty("program.tab.title"));
        tabbedPane.setTitleAt(1, UITexts.getProperty("member.tab.title"));

        // program tab labels and titles
        programTab.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1), UITexts.getProperty("program.tab.title"))
        );
        informationLabel.setText(UITexts.getProperty("requirement.details.label"));
        programStartDayLabel.setText(UITexts.getProperty("day.spinner.label"));
        programStartMonthLabel.setText(UITexts.getProperty("month.dropdown.label"));
        programStartYearLabel.setText(UITexts.getProperty("year.spinner.label"));
        serviceMeetingLabel.setText(UITexts.getProperty("service.meeting.dropdown.label"));
        sundayMeetingLabel.setText(UITexts.getProperty("sunday.meeting.dropdown.label"));
        otherSundayMeetingDayCheckbox.setText(UITexts.getProperty("other.sunday.meeting.checkbox.label"));
        howManyWeeksSpinnerLabel.setText(UITexts.getProperty("how.many.weeks.spinner.label"));
        // member tab labels and titles
        newMemberPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1), UITexts.getProperty("member.border.title"))
        );
        firstNameFieldLabel.setText(UITexts.getProperty("first.name.textfield.label"));
        lastNameFieldLabel.setText(UITexts.getProperty("last.name.textfield.label"));
        stageCheckBox.setText(UITexts.getProperty("stage.checkbox.label"));
        micCheckBox.setText(UITexts.getProperty("microphone.checkbox.label"));
        secondHallCheckBox.setText(UITexts.getProperty("second.hall.checkbox.label"));
        sundayExceptionCheckBox.setText(UITexts.getProperty("sunday.exception.checkbox.label"));

        stageCheckBox.setToolTipText(UITexts.getProperty("stage.checkbox.tooltip"));
        micCheckBox.setToolTipText(UITexts.getProperty("mic.checkbox.tooltip"));
        secondHallCheckBox.setToolTipText(UITexts.getProperty("second.hall.checkbox.tooltip"));
        sundayExceptionCheckBox.setToolTipText(UITexts.getProperty("sunday.exception.checkbox.tooltip"));

        addMemberButton.setText(UITexts.getProperty("add.button.text"));
        updateMemberButton.setText(UITexts.getProperty("update.button.title"));
        removeMemberButton.setText(UITexts.getProperty("remove.button.title"));
        generateButton.setText(UITexts.getProperty("generate.button.text"));

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

        setTitle(UITexts.getProperty("generator.frame.title"));

        addJMenuBar();

        tablePanel.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.GRAY, 1), UITexts.getProperty("member.table.border.title")
                )
        );
        tableModel.addColumn(UITexts.getProperty("id.column"));
        tableModel.addColumn(UITexts.getProperty("full.name.column"));
        tableModel.addColumn(UITexts.getProperty("stage.column"));
        tableModel.addColumn(UITexts.getProperty("mic.column"));
        tableModel.addColumn(UITexts.getProperty("hall2.column"));
        tableModel.addColumn(UITexts.getProperty("sunday.exception.column"));
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

        ArrayList<String> MEETING_DAYS = new ArrayList<>();
        MEETING_DAYS.add(UITexts.getProperty("day.7"));
        MEETING_DAYS.add(UITexts.getProperty("day.2"));
        MEETING_DAYS.add(UITexts.getProperty("day.3"));
        MEETING_DAYS.add(UITexts.getProperty("day.4"));
        MEETING_DAYS.add(UITexts.getProperty("day.5"));
        MEETING_DAYS.add(UITexts.getProperty("day.6"));
        MEETING_DAYS.add(UITexts.getProperty("day.1"));

        final String[] MONTHS = {
                UITexts.getProperty("month.1"), UITexts.getProperty("month.2"), UITexts.getProperty("month.3"),
                UITexts.getProperty("month.4"), UITexts.getProperty("month.5"), UITexts.getProperty("month.6"),
                UITexts.getProperty("month.7"), UITexts.getProperty("month.8"), UITexts.getProperty("month.9"),
                UITexts.getProperty("month.10"), UITexts.getProperty("month.11"), UITexts.getProperty("month.12")
        };

        monthComboBox.setModel(new DefaultComboBoxModel<>(MONTHS));
        weekendMeetingDayComboBox.setModel(new DefaultComboBoxModel<>((String[]) MEETING_DAYS.toArray()));
        // As "day.7" is added first (index = 0) in MEETING_DAYS, it must be removed before
        // adding the content of the ArrayList to the combobox that lists mid-week meeting days
        MEETING_DAYS.remove(0);
        midweekMeetingDayComboBox.setModel(new DefaultComboBoxModel<>((String[]) MEETING_DAYS.toArray()));

        addEventListenersToButtons();

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

        setContentPane(tabbedPane);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void addJMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        PreferenceForm preferenceForm = new PreferenceForm(this, UITexts);
        AboutForm aboutForm = new AboutForm(this, UITexts);

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
    }

    private void addEventListenersToButtons() {
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
                    JOptionPane.showMessageDialog(frame, UITexts.getProperty("incomplete.member.name.message"),
                            UITexts.getProperty("error.dialogue.title"), JOptionPane.ERROR_MESSAGE);
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

    private void updateMemberDuplicateAttributeOnUpdate(Member member, String oldName) throws SQLException {
        updateDuplicateAttributeOnAddition(member);
        member.setFirstName(oldName);
        updateDuplicateAttributeOnDelete(member);
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setEnabled(true);
        programTab = new JPanel();
        programTab.setLayout(new GridLayoutManager(7, 4, new Insets(0, 0, 0, 0), -1, -1));
        programTab.setEnabled(true);
        Font programTabFont = this.$$$getFont$$$("Maven Pro", -1, 12, programTab.getFont());
        if (programTabFont != null) programTab.setFont(programTabFont);
        tabbedPane.addTab(ResourceBundle.getBundle("UITexts").getString("program.tab.title"), programTab);
        programTab.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "", TitledBorder.LEFT, TitledBorder.TOP, this.$$$getFont$$$("Maven Pro", -1, 12, programTab.getFont()), new Color(-10128514)));
        programStartDayLabel = new JLabel();
        this.$$$loadLabelText$$$(programStartDayLabel, ResourceBundle.getBundle("UITexts").getString("day.spinner.label"));
        programTab.add(programStartDayLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        daySpinner = new JSpinner();
        Font daySpinnerFont = this.$$$getFont$$$(null, -1, -1, daySpinner.getFont());
        if (daySpinnerFont != null) daySpinner.setFont(daySpinnerFont);
        daySpinner.setToolTipText("");
        programTab.add(daySpinner, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        yearSpinner = new JSpinner();
        yearSpinner.setToolTipText("");
        programTab.add(yearSpinner, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        programStartMonthLabel = new JLabel();
        this.$$$loadLabelText$$$(programStartMonthLabel, ResourceBundle.getBundle("UITexts").getString("month.dropdown.label"));
        programTab.add(programStartMonthLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        programStartYearLabel = new JLabel();
        this.$$$loadLabelText$$$(programStartYearLabel, ResourceBundle.getBundle("UITexts").getString("year.spinner.label"));
        programTab.add(programStartYearLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        serviceMeetingLabel = new JLabel();
        serviceMeetingLabel.setHorizontalAlignment(0);
        serviceMeetingLabel.setHorizontalTextPosition(0);
        this.$$$loadLabelText$$$(serviceMeetingLabel, ResourceBundle.getBundle("UITexts").getString("service.meeting.dropdown.label"));
        programTab.add(serviceMeetingLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        midweekMeetingDayComboBox = new JComboBox();
        midweekMeetingDayComboBox.setEnabled(true);
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        midweekMeetingDayComboBox.setModel(defaultComboBoxModel1);
        midweekMeetingDayComboBox.setToolTipText("");
        programTab.add(midweekMeetingDayComboBox, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        howManyWeeksSpinnerLabel = new JLabel();
        this.$$$loadLabelText$$$(howManyWeeksSpinnerLabel, ResourceBundle.getBundle("UITexts").getString("how.many.weeks.spinner.label"));
        programTab.add(howManyWeeksSpinnerLabel, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        howManyWeeksSpinner = new JSpinner();
        programTab.add(howManyWeeksSpinner, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        programTab.add(spacer1, new GridConstraints(6, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        monthComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        monthComboBox.setModel(defaultComboBoxModel2);
        programTab.add(monthComboBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        weekendMeetingDayComboBox = new JComboBox();
        weekendMeetingDayComboBox.setEnabled(false);
        final DefaultComboBoxModel defaultComboBoxModel3 = new DefaultComboBoxModel();
        weekendMeetingDayComboBox.setModel(defaultComboBoxModel3);
        programTab.add(weekendMeetingDayComboBox, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sundayMeetingLabel = new JLabel();
        this.$$$loadLabelText$$$(sundayMeetingLabel, ResourceBundle.getBundle("UITexts").getString("sunday.meeting.dropdown.label"));
        programTab.add(sundayMeetingLabel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        otherSundayMeetingDayCheckbox = new JCheckBox();
        this.$$$loadButtonText$$$(otherSundayMeetingDayCheckbox, ResourceBundle.getBundle("UITexts").getString("other.sunday.meeting.checkbox.label"));
        programTab.add(otherSundayMeetingDayCheckbox, new GridConstraints(4, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        generateButton = new JButton();
        Font generateButtonFont = UIManager.getFont("Button.font");
        if (generateButtonFont != null) generateButton.setFont(generateButtonFont);
        generateButton.setIcon(new ImageIcon(getClass().getResource("/icons/generateExcel.png")));
        this.$$$loadButtonText$$$(generateButton, ResourceBundle.getBundle("UITexts").getString("generate.button.text"));
        programTab.add(generateButton, new GridConstraints(5, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 18, false));
        informationLabel = new JLabel();
        this.$$$loadLabelText$$$(informationLabel, ResourceBundle.getBundle("UITexts").getString("requirement.details.label"));
        programTab.add(informationLabel, new GridConstraints(1, 3, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        memberTab = new JPanel();
        memberTab.setLayout(new GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane.addTab(ResourceBundle.getBundle("UITexts").getString("member.tab.title"), memberTab);
        newMemberPanel = new JPanel();
        newMemberPanel.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), -1, -1));
        Font newMemberPanelFont = this.$$$getFont$$$("Maven Pro", -1, 12, newMemberPanel.getFont());
        if (newMemberPanelFont != null) newMemberPanel.setFont(newMemberPanelFont);
        memberTab.add(newMemberPanel, new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        newMemberPanel.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.LEFT, TitledBorder.TOP, this.$$$getFont$$$("Maven Pro", -1, 12, newMemberPanel.getFont()), new Color(-10128514)));
        firstNameFieldLabel = new JLabel();
        this.$$$loadLabelText$$$(firstNameFieldLabel, ResourceBundle.getBundle("UITexts").getString("first.name.textfield.label"));
        firstNameFieldLabel.setToolTipText("");
        newMemberPanel.add(firstNameFieldLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        FirstNameTextField = new JTextField();
        FirstNameTextField.setToolTipText("");
        newMemberPanel.add(FirstNameTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        lastNameFieldLabel = new JLabel();
        this.$$$loadLabelText$$$(lastNameFieldLabel, ResourceBundle.getBundle("UITexts").getString("last.name.textfield.label"));
        newMemberPanel.add(lastNameFieldLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lastNameTextField = new JTextField();
        lastNameTextField.setToolTipText("");
        newMemberPanel.add(lastNameTextField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        stageCheckBox = new JCheckBox();
        this.$$$loadButtonText$$$(stageCheckBox, ResourceBundle.getBundle("UITexts").getString("stage.checkbox.label"));
        stageCheckBox.setToolTipText("");
        newMemberPanel.add(stageCheckBox, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        micCheckBox = new JCheckBox();
        this.$$$loadButtonText$$$(micCheckBox, ResourceBundle.getBundle("UITexts").getString("microphone.checkbox.label"));
        micCheckBox.setToolTipText("");
        newMemberPanel.add(micCheckBox, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        secondHallCheckBox = new JCheckBox();
        this.$$$loadButtonText$$$(secondHallCheckBox, ResourceBundle.getBundle("UITexts").getString("second.hall.checkbox.label"));
        secondHallCheckBox.setToolTipText("");
        newMemberPanel.add(secondHallCheckBox, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sundayExceptionCheckBox = new JCheckBox();
        this.$$$loadButtonText$$$(sundayExceptionCheckBox, ResourceBundle.getBundle("UITexts").getString("second.hall.checkbox.label"));
        sundayExceptionCheckBox.setToolTipText("");
        newMemberPanel.add(sundayExceptionCheckBox, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addMemberButton = new JButton();
        addMemberButton.setIcon(new ImageIcon(getClass().getResource("/icons/addMember.png")));
        this.$$$loadButtonText$$$(addMemberButton, ResourceBundle.getBundle("UITexts").getString("add.button.text"));
        newMemberPanel.add(addMemberButton, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tablePanel = new JPanel();
        tablePanel.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        Font tablePanelFont = this.$$$getFont$$$("Maven Pro", -1, 12, tablePanel.getFont());
        if (tablePanelFont != null) tablePanel.setFont(tablePanelFont);
        memberTab.add(tablePanel, new GridConstraints(1, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        scrollPane = new JScrollPane();
        scrollPane.setEnabled(false);
        scrollPane.setHorizontalScrollBarPolicy(32);
        scrollPane.setVerticalScrollBarPolicy(22);
        tablePanel.add(scrollPane, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        membersTable = new JTable();
        membersTable.setAutoCreateRowSorter(true);
        membersTable.setAutoResizeMode(4);
        membersTable.setDropMode(DropMode.ON);
        membersTable.setEnabled(true);
        membersTable.setFillsViewportHeight(true);
        Font membersTableFont = UIManager.getFont("Table.font");
        if (membersTableFont != null) membersTable.setFont(membersTableFont);
        membersTable.setGridColor(new Color(-10128514));
        membersTable.setShowHorizontalLines(true);
        membersTable.setShowVerticalLines(true);
        membersTable.setToolTipText("");
        scrollPane.setViewportView(membersTable);
        updateMemberButton = new JButton();
        updateMemberButton.setIcon(new ImageIcon(getClass().getResource("/icons/updateMember.png")));
        this.$$$loadButtonText$$$(updateMemberButton, ResourceBundle.getBundle("UITexts").getString("update.button.title"));
        tablePanel.add(updateMemberButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        removeMemberButton = new JButton();
        removeMemberButton.setIcon(new ImageIcon(getClass().getResource("/icons/deleteMember.png")));
        this.$$$loadButtonText$$$(removeMemberButton, ResourceBundle.getBundle("UITexts").getString("remove.button.title"));
        tablePanel.add(removeMemberButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        tablePanel.add(spacer2, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        programStartDayLabel.setLabelFor(daySpinner);
        programStartMonthLabel.setLabelFor(monthComboBox);
        programStartYearLabel.setLabelFor(yearSpinner);
        serviceMeetingLabel.setLabelFor(midweekMeetingDayComboBox);
        howManyWeeksSpinnerLabel.setLabelFor(howManyWeeksSpinner);
        firstNameFieldLabel.setLabelFor(FirstNameTextField);
        lastNameFieldLabel.setLabelFor(lastNameTextField);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadLabelText$$$(JLabel component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setDisplayedMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadButtonText$$$(AbstractButton component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return tabbedPane;
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
                    JOptionPane.showMessageDialog(frame, UITexts.getProperty("user.canceled.operation.message"),
                            "", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case 0: // success
                    JOptionPane.showMessageDialog(frame, UITexts.getProperty("schedule.created.message"),
                            UITexts.getProperty("success.dialogue.title"), JOptionPane.INFORMATION_MESSAGE);
                    break;
                case 1: // could not save file
                    JOptionPane.showMessageDialog(frame, UITexts.getProperty("could.not.save.file.message"),
                            UITexts.getProperty("error.dialogue.title"), JOptionPane.ERROR_MESSAGE);
                    break;
                case 2: // database has 0 records (empty array)
                    JOptionPane.showMessageDialog(frame, UITexts.getProperty("no.members.found.message"),
                            UITexts.getProperty("error.dialogue.title"), JOptionPane.ERROR_MESSAGE);
                    break;
                default: // unknown problem
                    JOptionPane.showMessageDialog(frame, UITexts.getProperty("unknown.problem.message"),
                            UITexts.getProperty("error.dialogue.title"), JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
    }
}
