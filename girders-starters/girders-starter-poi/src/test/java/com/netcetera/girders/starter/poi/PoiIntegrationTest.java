package com.netcetera.girders.starter.poi;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined.GREY_25_PERCENT;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for the Girders POI module.
 */
@Slf4j
class PoiIntegrationTest {

  /**
   * Number of rows for the test workbook.
   */
  private static final int ROWS = 10;

  /**
   * Number of columns for the test workbook.
   */
  private static final int COLUMNS = 5;

  /**
   * Test the generation of an Excel workbook.
   *
   * @throws IOException if the output could not be written
   */
  @Test
  void testExcelGeneration() throws IOException {

    // prepare the workbook
    try (final HSSFWorkbook workbook = new HSSFWorkbook()) {
      HSSFSheet sheet = workbook.createSheet("Test Sheet");

      // create header style
      HSSFCellStyle headerStyle = workbook.createCellStyle();
      headerStyle.setFillForegroundColor(GREY_25_PERCENT.getIndex());
      headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      HSSFFont headerFont = workbook.createFont();
      headerFont.setBold(true);
      headerStyle.setFont(headerFont);

      // create title style
      HSSFCellStyle contentStyle = workbook.createCellStyle();
      HSSFFont font = workbook.createFont();
      font.setBold(false);
      contentStyle.setFont(font);

      // header row;
      HSSFRow headerRow = sheet.createRow(0);
      for (int j = 0; j < COLUMNS; ++j) {
        HSSFCell cell = headerRow.createCell(j);
        cell.setCellStyle(headerStyle);
        cell.setCellValue(new HSSFRichTextString("Column " + j));
      }

      // content rows
      for (int i = 1; i < ROWS; ++i) {
        HSSFRow row = sheet.createRow(i);
        for (int j = 0; j < 5; ++j) {
          HSSFCell cell = row.createCell(j);
          cell.setCellStyle(contentStyle);
          cell.setCellValue(new HSSFRichTextString("Cell " + i + '/' + j));
        }
      }

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      workbook.write(outputStream);
      outputStream.flush();
      outputStream.close();

      logger.info("document of {} bytes written", outputStream.size());
      assertTrue(outputStream.size() > 0);
    }
  }
}
