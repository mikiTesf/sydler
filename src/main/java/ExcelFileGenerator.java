import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
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
    private final Map<Integer, String> AMMonths;
    private final Map<Integer, String> days;
    // XSSFWorkbook, XSSFSheet, XSSFRow, XSSFCell
    private ExcelFileGenerator() {
        Populate populate = new Populate(1);
        names = populate.getNameGrid();
        // month to name map to translate a month's value (int) into an Amharic String
        AMMonths = new HashMap<>(12);
        AMMonths.put(1, "ጥር");
        AMMonths.put(2, "የካቲት");
        AMMonths.put(3, "መጋቢት");
        AMMonths.put(4, "ሚያዝያ");
        AMMonths.put(5, "ግንቦት");
        AMMonths.put(6, "ሰኔ");
        AMMonths.put(7, "ሐምሌ");
        AMMonths.put(8, "ነሐሴ");
        AMMonths.put(9, "መስከረም");
        AMMonths.put(10, "ጥቅምት");
        AMMonths.put(11, "ህዳር");
        AMMonths.put(12, "ታህሳሥ");

        days = new HashMap<>(7);
        days.put(1, "ሰኞ");
        days.put(2, "ማክሰኞ");
        days.put(3, "ዕሮብ");
        days.put(4, "ሐሙስ");
        days.put(5, "አርብ");
        days.put(6, "ቅዳሜ");
        days.put(7, "እሁድ");
    }

    private void makeExcel() {
        // The excel document
        XSSFWorkbook schedule = new XSSFWorkbook();
        // The excel sheet to put data in
        XSSFSheet sheet = schedule.createSheet("schedule sheet");
        // put column names at the header of the sheet
        Row row = sheet.createRow((short) 0);
        // XSSFFont object with property: bold
        XSSFFont font  = schedule.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        // bold font style
        XSSFCellStyle boldFontStyle = schedule.createCellStyle();
        boldFontStyle.setFont(font);
        // center alignment cellStyle
        CellStyle centerStyle = schedule.createCellStyle();
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        // here goes the header titles
        XSSFRichTextString headerString = new XSSFRichTextString();

        headerString.setString("ሳምንት");
        headerString.applyFont(font);
        row.createCell(0).setCellValue(headerString);
        row.getCell(0).setCellStyle(centerStyle);

        headerString.setString("ቀን");
        headerString.applyFont(font);
        row.createCell(1).setCellValue(headerString);
        row.getCell(1).setCellStyle(centerStyle);

        headerString.setString("መድረክ");
        headerString.applyFont(font);
        row.createCell(2).setCellValue(headerString);
        row.getCell(2).setCellStyle(centerStyle);

        headerString.setString("በመጀመሪያው ዙር");
        headerString.applyFont(font);
        row.createCell(3).setCellValue(headerString);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 3, 4));
        row.getCell(3).setCellStyle(centerStyle);

        headerString.setString("በሁለተኛው ዙር");
        headerString.applyFont(font);
        row.createCell(5).setCellValue(headerString);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 5, 6));
        row.getCell(5).setCellStyle(centerStyle);

        headerString.setString("በሁለተኛው አዳራሽ");
        headerString.applyFont(font);
        row.createCell(7).setCellValue(headerString);
        row.getCell(7).setCellStyle(centerStyle);

        // here the starting date will be initialized
        LocalDateTime midWeek = LocalDateTime.of(2018, 8, 13, 0, 0);
        String weekMonth, monthOnSunday;
        // the following is the main for loop that fills the schedule by populating it with
        // the week spans, member names and date information
        for (int day = 0; day < names.length; day++) {
            Row row1 = sheet.createRow((short) (day + 1));
            // The Monday and Sunday of a week may reside in different months.
            // To include the name of the next month in the cell, the month after
            // 6 days must be calculated and compared with the month on Monday
            weekMonth     = AMMonths.get(midWeek.getMonthValue());
            monthOnSunday = AMMonths.get(midWeek.plusDays(6).getMonthValue());
            // Here, the months are compared and the appropriate week-span is put.
            // The if block is important because week-spans are calculated once for
            // every week. No need to calculate again on the sunday of the same week
            if (day % 2 == 0) {
                if (weekMonth.equals(monthOnSunday))
                    row1.createCell(0).setCellValue(weekMonth + " " + midWeek.getDayOfMonth() + " - " + midWeek.plusDays(6).getDayOfMonth());
                else
                    row1.createCell(0).setCellValue(weekMonth + " " + midWeek.getDayOfMonth() + " - " + monthOnSunday + " " + midWeek.plusDays(6).getDayOfMonth());
                sheet.addMergedRegion(new CellRangeAddress(row1.getRowNum(), row1.getRowNum() + 1, 0, 0));
                row1.getCell(0).setCellStyle(boldFontStyle);
                // change date for next week
                midWeek = midWeek.plusDays(7);
            }
            // put the name of the day in the next cell
            if (day % 2 == 0) // even = mid-week meeting
                row1.createCell(1).setCellValue(" " + days.get(2));
            else
                row1.createCell(1).setCellValue(" " + days.get(7));
            // put the names of members in the next loop
            for (int j = 2, k = 0; j < 8; j++, k++)
                if (!(names[day][k] == null))
                    row1.createCell(j).setCellValue(" " + names[day][k]);
        }
        // auto-size columns to fit the text inside the cells
        sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6); sheet.autoSizeColumn(7);

        try {
            FileOutputStream out = new FileOutputStream(new File("schedule.xlsx"));
            schedule.write(out);
            out.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        ExcelFileGenerator generator = new ExcelFileGenerator();
        generator.makeExcel();
    }
}
