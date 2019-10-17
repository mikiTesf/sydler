package controller;

import domain.Member;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class ExcelFileGenerator {
    private final String[][] names;
    private final HashMap<Integer, String> AMMonths;
    private final HashMap<String, String> ENMonths;
    private XSSFWorkbook schedule;
    private XSSFSheet sheet;
    private LocalDateTime beginDate;
    private final int WEEK_SPAN          = 0;
    private final int MEETING_DAY_NAME   = 1;
    private final int STAGE              = 2;
    private final int FIRST_ROUND_LEFT   = 3;
    private final int FIRST_ROUND_RIGHT  = 4;
    private final int SECOND_ROUND_LEFT  = 5;
    private final int SECOND_ROUND_RIGHT = 6;
    private final int SECOND_HALL        = 7;
    private final Properties EXCEL_TEXTS = new Properties();

    public ExcelFileGenerator(int weeks, LocalDateTime beginDate) {

        try {
            EXCEL_TEXTS.load(getClass().getResourceAsStream("/excelTexts.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        names = new Populate(weeks).getNameGrid();
        this.beginDate = beginDate;

        AMMonths = new HashMap<>(12);
        AMMonths.put(1, getAbbreviatedMonthName(EXCEL_TEXTS.getProperty("SEPTEMBER")));
        AMMonths.put(2, getAbbreviatedMonthName(EXCEL_TEXTS.getProperty("OCTOBER")));
        AMMonths.put(3, getAbbreviatedMonthName(EXCEL_TEXTS.getProperty("NOVEMBER")));
        AMMonths.put(4, getAbbreviatedMonthName(EXCEL_TEXTS.getProperty("DECEMBER")));
        AMMonths.put(5, getAbbreviatedMonthName(EXCEL_TEXTS.getProperty("JANUARY")));
        AMMonths.put(6, getAbbreviatedMonthName(EXCEL_TEXTS.getProperty("FEBRUARY")));
        AMMonths.put(7, getAbbreviatedMonthName(EXCEL_TEXTS.getProperty("MARCH")));
        AMMonths.put(8, getAbbreviatedMonthName(EXCEL_TEXTS.getProperty("APRIL")));
        AMMonths.put(9, getAbbreviatedMonthName(EXCEL_TEXTS.getProperty("MAY")));
        AMMonths.put(10, getAbbreviatedMonthName(EXCEL_TEXTS.getProperty("JUNE")));
        AMMonths.put(11, getAbbreviatedMonthName(EXCEL_TEXTS.getProperty("JULY")));
        AMMonths.put(12, getAbbreviatedMonthName(EXCEL_TEXTS.getProperty("AUGUST")));

        ENMonths = new HashMap<>(12);
        ENMonths.put("SEPTEMBER", EXCEL_TEXTS.getProperty("SEPTEMBER"));
        ENMonths.put("OCTOBER", EXCEL_TEXTS.getProperty("OCTOBER"));
        ENMonths.put("NOVEMBER", EXCEL_TEXTS.getProperty("NOVEMBER"));
        ENMonths.put("DECEMBER", EXCEL_TEXTS.getProperty("DECEMBER"));
        ENMonths.put("JANUARY", EXCEL_TEXTS.getProperty("JANUARY"));
        ENMonths.put("FEBRUARY", EXCEL_TEXTS.getProperty("FEBRUARY"));
        ENMonths.put("MARCH", EXCEL_TEXTS.getProperty("MARCH"));
        ENMonths.put("APRIL", EXCEL_TEXTS.getProperty("APRIL"));
        ENMonths.put("MAY", EXCEL_TEXTS.getProperty("MAY"));
        ENMonths.put("JUNE", EXCEL_TEXTS.getProperty("JUNE"));
        ENMonths.put("JULY", EXCEL_TEXTS.getProperty("JULY"));
        ENMonths.put("AUGUST", EXCEL_TEXTS.getProperty("AUGUST"));
    }

    private String getAbbreviatedMonthName(String monthName) {
        return monthName.length() > 2 ? monthName.substring(0, 3) : monthName;
    }

    public int makeExcel(
            String midweekMeetingDay,
            String weekendMeetingDay,
            List<Member> allMembers,
            String savePath
    ) {
        final int SUCCESS_STATUS = 0, COULD_NOT_SAVE_FILE_ERROR = 1, EMPTY_ARRAY_ERROR = 2;

        if (names == null) return EMPTY_ARRAY_ERROR;

        File programsFolder = new File(savePath + File.separator + "SSS_Program(s)");

        if (!programsFolder.exists()) {
            programsFolder.mkdir();
        }

        savePath += File.separator + "SSS_Program(s)"
                + File.separator + "SSS"
                + "_" + beginDate.getMonth()
                + "_" + beginDate.getYear();

        for (Member member : allMembers) {
            if (!member.hasAtLeastOneRole()) continue;

            // SSS - Sound System Schedule
            String filePath = String.format("%s_%s_%s_.xlsx", savePath, member.getFirstName(), member.getLastName());

            schedule = new XSSFWorkbook();
            // setup sheet (page) properties
            sheet = schedule.createSheet("schedule");
            sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);

            insertSheetTitle();
            initializeColumnHeaders();
            populateSheetWithWeekSpansAndNames(midweekMeetingDay, weekendMeetingDay);
            highlightMemberNames(sheet, member.getFirstName());
            resizeColumnsAndFitContent();

            try {
                FileOutputStream out = new FileOutputStream(new File(filePath));
                schedule.write(out);
                System.out.println("excel file saved under " + filePath + "...");
                out.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return COULD_NOT_SAVE_FILE_ERROR;
            }
        }
        return SUCCESS_STATUS;
    }

    private boolean isMidweek(int day) {
        return (day % 2) == 0;
    }

    private void insertSheetTitle() {
        // title at the top of sheet
        Row programTitleRow = sheet.createRow(0);
        XSSFFont font = schedule.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 24);

        XSSFRichTextString formattedText = new XSSFRichTextString();
        formattedText.setString(
                "የአዲስ ሰፈር ጉባኤ የድምጽ ክፍል ፕሮግራም\n"
                        + "("  + ENMonths.get(beginDate.getMonth().toString()) + " " + beginDate.getDayOfMonth() + ", " + beginDate.getYear()
                /* Each week has two days. "names.length" is the sum of mid-week and Sunday meeting days. Therefore,
                   the total number of weeks is equal to (names.length / 2) */
                        + " → " + ENMonths.get(beginDate.plusWeeks((names.length / 2) - 1).plusDays(6).getMonth().toString())
                        + " "  + beginDate.plusWeeks(names.length / 2 - 1).plusDays(6).getDayOfMonth()
                        + ", " + beginDate.plusWeeks(names.length / 2 - 1).plusDays(6).getYear() + ") "
        );
        formattedText.applyFont(font);
        font.setFontHeightInPoints((short) 14);
        formattedText.applyFont(formattedText.getString().indexOf('('), formattedText.getString().length() - 1, font);
        programTitleRow.createCell(0).setCellValue(formattedText);
        programTitleRow.getCell(0).setCellStyle(getCellStyle(true, false, false));
        sheet.addMergedRegion(new CellRangeAddress(programTitleRow.getRowNum(), programTitleRow.getRowNum(), WEEK_SPAN, SECOND_HALL));
    }

    private void initializeColumnHeaders() {
        XSSFRichTextString formattedText = new XSSFRichTextString();
        XSSFFont font = schedule.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);

        Row headerRow = sheet.createRow(sheet.getLastRowNum() + 1);
        CellStyle headerRowCellStyle = getCellStyle(true, true, true);
        // put column names at the header of the sheet
        font.setFontHeightInPoints((short) 10);
        formattedText.setString(EXCEL_TEXTS.getProperty("week.span"));
        formattedText.applyFont(font);
        headerRow.createCell(WEEK_SPAN).setCellValue(formattedText);
        headerRow.getCell(WEEK_SPAN).setCellStyle(headerRowCellStyle);

        formattedText.setString(EXCEL_TEXTS.getProperty("day"));
        formattedText.applyFont(font);
        headerRow.createCell(MEETING_DAY_NAME).setCellValue(formattedText);
        headerRow.getCell(MEETING_DAY_NAME).setCellStyle(headerRowCellStyle);

        formattedText.setString(EXCEL_TEXTS.getProperty("stage"));
        formattedText.applyFont(font);
        headerRow.createCell(STAGE).setCellValue(formattedText);
        headerRow.getCell(STAGE).setCellStyle(headerRowCellStyle);

        formattedText.setString(EXCEL_TEXTS.getProperty("first.round"));
        insertRoundDivisionHeader
                (formattedText, font, headerRow, headerRowCellStyle, FIRST_ROUND_LEFT, FIRST_ROUND_RIGHT);

        formattedText.setString(EXCEL_TEXTS.getProperty("second.round"));
        insertRoundDivisionHeader
                (formattedText, font, headerRow, headerRowCellStyle, SECOND_ROUND_LEFT, SECOND_ROUND_RIGHT);

        formattedText.setString(EXCEL_TEXTS.getProperty("second.hall"));
        formattedText.applyFont(font);
        headerRow.createCell(SECOND_HALL).setCellValue(formattedText);
        headerRow.getCell(SECOND_HALL).setCellStyle(headerRowCellStyle);
    }

    private void insertRoundDivisionHeader(
            XSSFRichTextString formattedText,
            XSSFFont font,
            Row headerRow,
            CellStyle headerRowCellStyle,
            int roleIndexLeft,
            int roleIndexRight)
    {
        formattedText.applyFont(font);
        headerRow.createCell(roleIndexLeft).setCellValue(formattedText);
        sheet.addMergedRegion(new CellRangeAddress
                (headerRow.getRowNum(), headerRow.getRowNum(), roleIndexLeft, roleIndexRight));
        headerRow.getCell(roleIndexLeft).setCellStyle(headerRowCellStyle);
    }

    private void populateSheetWithWeekSpansAndNames(String midweekMeetingDay, String weekendMeetingDay) {
        XSSFRichTextString formattedText = new XSSFRichTextString();
        XSSFFont font = schedule.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);

        String weekMonth, monthOnSunday;
        /* the following is the main for loop that fills the schedule by populating it with
         the week spans, member names and beginDate information*/
        for (int day = 0; day < names.length; day++) {
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            /* The Monday and Sunday of a week may reside in different months.
             To include the name of the next month in the cell, the month after
             6 days must be calculated and compared with the month on Monday */
            weekMonth     = AMMonths.get(beginDate.getMonthValue());
            monthOnSunday = AMMonths.get(beginDate.plusDays(6).getMonthValue());
            /* Here, the months are compared and the appropriate week-span is put.
             The if block is important because week-spans are calculated once for
             every week. No need to calculate again on the Sunday of the same week*/
            row.createCell(WEEK_SPAN);
            row.createCell(MEETING_DAY_NAME);
            if (isMidweek(day)) {
                if (weekMonth.equals(monthOnSunday)) {
                    formattedText.setString
                            (weekMonth + " " + beginDate.getDayOfMonth() + " - " + beginDate.plusDays(6).getDayOfMonth());
                } else {
                    formattedText.setString
                            (weekMonth + " " + beginDate.getDayOfMonth() + " - " + monthOnSunday + " " + beginDate.plusDays(6).getDayOfMonth());
                }
                formattedText.applyFont(font);
                row.getCell(WEEK_SPAN).setCellValue(formattedText);
                sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum() + 1, WEEK_SPAN, WEEK_SPAN));
                // the name of the day comes on the next cell
                row.createCell(MEETING_DAY_NAME).setCellValue(" " + midweekMeetingDay);
            } else {
                row.createCell(MEETING_DAY_NAME).setCellValue(" " + weekendMeetingDay);
                // change beginDate for next week
                beginDate = beginDate.plusWeeks(1);
            }
            row.getCell(WEEK_SPAN).setCellStyle(getCellStyle(true, true, false));
            row.getCell(MEETING_DAY_NAME).setCellStyle(getCellStyle(false, true, false));
            // put the names of members in the next loop
            for (int column = STAGE, role = 0; column <= SECOND_HALL; column++, role++) {
                row.createCell(column);
                row.getCell(column).setCellStyle(getCellStyle(false, true, false));
                if (!(names[day][role] == null)) {
                    row.getCell(column).setCellValue(names[day][role]);
                }
            }
        }

        System.out.println("excel sheet populated...");
    }

    private void resizeColumnsAndFitContent() {
        sheet.autoSizeColumn(WEEK_SPAN);
        sheet.autoSizeColumn(MEETING_DAY_NAME);
        sheet.autoSizeColumn(STAGE);
        sheet.autoSizeColumn(FIRST_ROUND_RIGHT);
        sheet.autoSizeColumn(FIRST_ROUND_LEFT);
        sheet.autoSizeColumn(SECOND_ROUND_RIGHT);
        sheet.autoSizeColumn(SECOND_ROUND_LEFT);
        sheet.autoSizeColumn(SECOND_HALL);
        sheet.setFitToPage(true);
    }

    private void highlightMemberNames(XSSFSheet scheduleSheet, String memberName) {
        for (Row row : scheduleSheet) {
            for (Cell cell : row) {
                if (cell.getStringCellValue().equals(memberName)) {
                    cell.getCellStyle().setFillBackgroundColor(IndexedColors.LIGHT_GREEN.index);
                    cell.getCellStyle().setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
                    cell.getCellStyle().setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    // for some unknown reason the Cell's horizontal alignment is being reset to LEFT
                    cell.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
                }
            }
        }
    }

    private CellStyle getCellStyle(boolean centerAligned, boolean fullyBordered, boolean filledBackground) {
        XSSFCellStyle cellStyle = schedule.createCellStyle();

        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(centerAligned ? HorizontalAlignment.CENTER : HorizontalAlignment.LEFT);

        if (fullyBordered) {
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
        }

        if (filledBackground) {
            cellStyle.setFillBackgroundColor(new XSSFColor(new Color(200, 200, 200)));
            cellStyle.setFillForegroundColor(new XSSFColor(new Color(200, 200, 200)));
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }

        return cellStyle;
    }
}
