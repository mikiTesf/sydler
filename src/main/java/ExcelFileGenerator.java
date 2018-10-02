import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

class ExcelFileGenerator {
    private final String[][] names;
    private final HashMap<Integer, String> AMMonths;
    private final HashMap ENMonths;
    private final HashMap<Integer, String> days;
    // common column indexes
    private final int WEEK_SPAN          = 0;
    private final int MEETING_DAY_NAME   = 1;
    private final int STAGE              = 2;
    private final int FIRST_ROUND_RIGHT  = 3;
    private final int FIRST_ROUND_LEFT   = 4;
    private final int SECOND_ROUND_RIGHT = 5;
    private final int SECOND_ROUND_LEFT  = 6;
    private final int SECOND_HALL        = 7;
    private XSSFWorkbook schedule;

    ExcelFileGenerator(int weeks) {
        names = new Populate(weeks).getNameGrid();
        // month to name map to translate a month's value (int) into an Amharic String
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

        ENMonths = new HashMap(12);
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

        days = new HashMap<>(7);
        days.put(1, "ሰኞ");
        days.put(2, "ማክሰኞ");
        days.put(3, "ዕሮብ");
        days.put(4, "ሐሙስ");
        days.put(5, "አርብ");
        days.put(6, "ቅዳሜ");
        days.put(7, "እሁድ");
    }

    boolean makeExcel(LocalDateTime date, String meetingDay, String savePath) {
        String filePath = savePath + "/" + date.getDayOfMonth() + "_" + date.getMonth() + "_" + date.getYear() + ".xlsx";
        // The excel document
        schedule = new XSSFWorkbook();
        // The excel sheet to put data in
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
                + ", " + date.getYear() + " → " + ENMonths.get(date.plusWeeks(names.length / 2 - 1).plusDays(6).getMonth().toString())
                + " "  + date.plusWeeks(names.length / 2 - 1).plusDays(6).getDayOfMonth()
                + ", " + date.plusWeeks(names.length / 2 - 1).plusDays(6).getYear() + ") "
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

        // here the starting date will be initialized
//        LocalDateTime date = LocalDateTime.of(year, month, day, 0, 0);
        String weekMonth, monthOnSunday;
        /* the following is the main for loop that fills the schedule by populating it with
         the week spans, member names and date information*/
        for (int day1 = 0; day1 < names.length; day1++) {
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            /* The Monday and Sunday of a week may reside in different months.
             To include the name of the next month in the cell, the month after
             6 days must be calculated and compared with the month on Monday */
            weekMonth     = AMMonths.get(date.getMonthValue());
            monthOnSunday = AMMonths.get(date.plusDays(6).getMonthValue());
            /* Here, the months are compared and the appropriate week-span is put.
             The if block is important because week-spans are calculated once for
             every week. No need to calculate again on the sunday of the same week*/
            row.createCell(WEEK_SPAN);
            if (day1 % 2 == 0) {
                if (weekMonth.equals(monthOnSunday))
                    formattedText.setString(weekMonth + " " + date.getDayOfMonth() + " - " + date.plusDays(6).getDayOfMonth());
                else
                    formattedText.setString(weekMonth + " " + date.getDayOfMonth() + " - " + monthOnSunday + " " + date.plusDays(6).getDayOfMonth());
                formattedText.applyFont(font);
                row.getCell(row.getLastCellNum() - 1).setCellValue(formattedText);
                sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum() + 1, 0, 0));
                // change date for next week
                date = date.plusDays(7);
            }
            row.getCell(row.getLastCellNum() - 1).setCellStyle(getCellStyle(true, true, false));
            // put the name of the day in the next cell
            if (day1 % 2 == 0) // even = mid-week meeting
                row.createCell(row.getLastCellNum()).setCellValue(" " + meetingDay);
            else
                row.createCell(row.getLastCellNum()).setCellValue(" " + days.get(7));
            row.getCell(row.getLastCellNum() - 1).setCellStyle(getCellStyle(false, true, false));
            // put the names of members in the next loop
            for (int j = STAGE, k = 0; j <= SECOND_HALL; j++, k++) {
                row.createCell(j);
                row.getCell(j).setCellStyle(getCellStyle(false, true, false));
                if (!(names[day1][k] == null)) {
                    row.getCell(j).setCellValue(" " + names[day1][k]);
                }
            }
        }
        System.out.println("excel sheet populated...");
        // auto-size columns to fit the text inside the cells
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
            return false;
        }
        return true;
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
            cellStyle.setFillBackgroundColor(new XSSFColor(new java.awt.Color(200, 200, 200)));
            cellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(200, 200, 200)));
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }

        return cellStyle;
    }
}
