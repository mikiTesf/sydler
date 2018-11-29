package controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

public class ExcelFileGenerator {
    private final String[][] names;
    private final HashMap<Integer, String> AMMonths;
    private final HashMap<String, String> ENMonths;
    private XSSFWorkbook schedule;
    private final HashMap<String, Integer> dayNameToNumber;

    public ExcelFileGenerator(int weeks) {
        names = new Populate(weeks).getNameGrid();

        dayNameToNumber = new HashMap<>();
        dayNameToNumber.put("ሰኞ", 1);
        dayNameToNumber.put("ማክሰኞ", 2);
        dayNameToNumber.put("ዕሮብ", 3);
        dayNameToNumber.put("ሐሙስ", 4);
        dayNameToNumber.put("አርብ", 5);
        dayNameToNumber.put("ቅዳሜ", 6);
        dayNameToNumber.put("እሁድ", 7);

        AMMonths = new HashMap<>(12);
        AMMonths.put(1, "ጥር");
        AMMonths.put(2, "የካ");
        AMMonths.put(3, "መጋ");
        AMMonths.put(4, "ሚያ");
        AMMonths.put(5, "ግን");
        AMMonths.put(6, "ሰኔ");
        AMMonths.put(7, "ሐም");
        AMMonths.put(8, "ነሐ");
        AMMonths.put(9, "መስ");
        AMMonths.put(10, "ጥቅ");
        AMMonths.put(11, "ህዳ");
        AMMonths.put(12, "ታህ");

        ENMonths = new HashMap<>(12);
        ENMonths.put("SEPTEMBER", "መስከረም");
        ENMonths.put("OCTOBER", "ጥቅምት");
        ENMonths.put("NOVEMBER", "ህዳር");
        ENMonths.put("DECEMBER", "ታህሳሥ");
        ENMonths.put("JANUARY", "ጥር");
        ENMonths.put("FEBRUARY", "የካቲት");
        ENMonths.put("MARCH", "መጋቢት");
        ENMonths.put("APRIL", "ሚያዝያ");
        ENMonths.put("MAY", "ግንቦት");
        ENMonths.put("JUNE", "ሰኔ");
        ENMonths.put("JULY", "ሐምሌ");
        ENMonths.put("AUGUST", "ነሐሴ");
    }

    public int makeExcel(LocalDateTime date, String midweekMeetingDay, String weekendMeetingDay, String savePath) {
        final int SUCCESS_STATUS = 0, COULD_NOT_SAVE_FILE_ERROR = 1, EMPTY_ARRAY_ERROR = 2;

        if (names == null) return EMPTY_ARRAY_ERROR;

        String filePath = savePath + "/" + date.getDayOfMonth() + "_" + date.getMonth() + "_" + date.getYear() + ".xlsx";
        final int daysBetweenMeetingDays = dayNameToNumber.get(weekendMeetingDay) - dayNameToNumber.get(midweekMeetingDay);
        final int WEEK_SPAN          = 0;
        final int MEETING_DAY_NAME   = 1;
        final int STAGE              = 2;
        final int FIRST_ROUND_RIGHT  = 3;
        final int FIRST_ROUND_LEFT   = 4;
        final int SECOND_ROUND_RIGHT = 5;
        final int SECOND_ROUND_LEFT  = 6;
        final int SECOND_HALL        = 7;

        schedule = new XSSFWorkbook();
        XSSFSheet sheet = schedule.createSheet("schedule sheet");
        // setup sheet (page) properties
        sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
        XSSFRichTextString formattedText = new XSSFRichTextString();
        XSSFFont font = schedule.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 24);
        // title at the top of sheet
        Row programTitleRow = sheet.createRow(0);
        formattedText.setString(
                "የአዲስ ሰፈር ጉባኤ የድምጽ ክፍል ፕሮግራም\n"
                + "("  + ENMonths.get(date.getMonth().toString()) + " " + date.getDayOfMonth()
                /* Each week has two days. [names.length] is the sum of mid-week and sunday meeting days. Therefore,
                   the total number of weeks is equal to [names.length / 2] */
                + ", " + date.getYear() + " → " + ENMonths.get(date.plusWeeks((names.length / 2) - 1).plusDays(daysBetweenMeetingDays).getMonth().toString())
                + " "  + date.plusWeeks(names.length / 2 - 1).plusDays(daysBetweenMeetingDays).getDayOfMonth()
                + ", " + date.plusWeeks(names.length / 2 - 1).plusDays(daysBetweenMeetingDays).getYear() + ") "
        );
        formattedText.applyFont(font);
        font.setFontHeightInPoints((short) 14);
        formattedText.applyFont(formattedText.getString().indexOf('('), formattedText.getString().length() - 1, font);
        programTitleRow.createCell(0).setCellValue(formattedText);
        programTitleRow.getCell(0).setCellStyle(getCellStyle(true, false, false));
        sheet.addMergedRegion(new CellRangeAddress(programTitleRow.getRowNum(), programTitleRow.getRowNum(), WEEK_SPAN, SECOND_HALL));
        // the titles in each column get initialized next
        Row headerRow = sheet.createRow(sheet.getLastRowNum() + 1);
        // put column names at the header of the sheet
        font.setFontHeightInPoints((short) 10);
        formattedText.setString("ሳምንት");
        formattedText.applyFont(font);
        headerRow.createCell(WEEK_SPAN).setCellValue(formattedText);
        headerRow.getCell(WEEK_SPAN).setCellStyle(getCellStyle(true, true, true));

        formattedText.setString("ቀን");
        formattedText.applyFont(font);
        headerRow.createCell(MEETING_DAY_NAME).setCellValue(formattedText);
        headerRow.getCell(MEETING_DAY_NAME).setCellStyle(getCellStyle(true, true, true));

        formattedText.setString("መድረክ");
        formattedText.applyFont(font);
        headerRow.createCell(STAGE).setCellValue(formattedText);
        headerRow.getCell(STAGE).setCellStyle(getCellStyle(true, true, true));

        formattedText.setString("በመጀመሪያው ዙር");
        formattedText.applyFont(font);
        headerRow.createCell(FIRST_ROUND_RIGHT).setCellValue(formattedText);
        sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), 3, 4));
        headerRow.getCell(FIRST_ROUND_RIGHT).setCellStyle(getCellStyle(true, true, true));

        formattedText.setString("በሁለተኛው ዙር");
        formattedText.applyFont(font);
        headerRow.createCell(SECOND_ROUND_RIGHT).setCellValue(formattedText);
        sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), 5, 6));
        headerRow.getCell(SECOND_ROUND_RIGHT).setCellStyle(getCellStyle(true, true, true));

        formattedText.setString("በሁለተኛው አዳራሽ");
        formattedText.applyFont(font);
        headerRow.createCell(SECOND_HALL).setCellValue(formattedText);
        headerRow.getCell(SECOND_HALL).setCellStyle(getCellStyle(true, true, true));

        String weekMonth, monthOnSunday;
        /* the following is the main for loop that fills the schedule by populating it with
         the week spans, member names and date information*/
        for (int day = 0; day < names.length; day++) {
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            /* The Monday and Sunday of a week may reside in different months.
             To include the name of the next month in the cell, the month after
             6 days must be calculated and compared with the month on Monday */
            weekMonth     = AMMonths.get(date.getMonthValue());
            monthOnSunday = AMMonths.get(date.plusDays(daysBetweenMeetingDays).getMonthValue());
            /* Here, the months are compared and the appropriate week-span is put.
             The if block is important because week-spans are calculated once for
             every week. No need to calculate again on the sunday of the same week*/
            row.createCell(WEEK_SPAN);
            if (isMidweek(day)) {
                if (weekMonth.equals(monthOnSunday))
                    formattedText.setString(weekMonth + " " + date.getDayOfMonth() + " - " + date.plusDays(daysBetweenMeetingDays).getDayOfMonth());
                else
                    formattedText.setString(weekMonth + " " + date.getDayOfMonth() + " - " + monthOnSunday + " " + date.plusDays(daysBetweenMeetingDays).getDayOfMonth());
                formattedText.applyFont(font);
                row.getCell(row.getLastCellNum() - 1).setCellValue(formattedText);
                sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum() + 1, 0, 0));
                // change date for next week
                date = date.plusDays(7);
            }
            row.getCell(row.getLastCellNum() - 1).setCellStyle(getCellStyle(true, true, false));
            // put the name of the day in the next cell
            if (isMidweek(day))
                row.createCell(row.getLastCellNum()).setCellValue(" " + midweekMeetingDay);
            else
                row.createCell(row.getLastCellNum()).setCellValue(" " + weekendMeetingDay);
            row.getCell(row.getLastCellNum() - 1).setCellStyle(getCellStyle(false, true, false));
            // put the names of members in the next loop
            for (int j = STAGE, k = 0; j <= SECOND_HALL; j++, k++) {
                row.createCell(j);
                row.getCell(j).setCellStyle(getCellStyle(false, true, false));
                if (!(names[day][k] == null)) {
                    row.getCell(j).setCellValue(" " + names[day][k]);
                }
            }
        }
        System.out.println("excel sheet populated...");
        
        sheet.autoSizeColumn(WEEK_SPAN);
        sheet.autoSizeColumn(MEETING_DAY_NAME);
        sheet.autoSizeColumn(STAGE);
        sheet.autoSizeColumn(FIRST_ROUND_RIGHT);
        sheet.autoSizeColumn(FIRST_ROUND_LEFT);
        sheet.autoSizeColumn(SECOND_ROUND_RIGHT);
        sheet.autoSizeColumn(SECOND_ROUND_LEFT);
        sheet.autoSizeColumn(SECOND_HALL);

        sheet.setFitToPage(true);

        try {
            FileOutputStream out = new FileOutputStream(new File(filePath));
            schedule.write(out);
            System.out.println("excel file saved under " + filePath + "...");
            out.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return COULD_NOT_SAVE_FILE_ERROR;
        }
        return SUCCESS_STATUS;
    }

    private boolean isMidweek(int day) {
        return day % 2 == 0;
    }

    private CellStyle getCellStyle(boolean centerAligned, boolean fullyBordered, boolean filledBackground) {
        XSSFCellStyle cellStyle = schedule.createCellStyle();

        cellStyle.setWrapText(true);
        cellStyle.setAlignment(centerAligned ? HorizontalAlignment.CENTER : HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

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
