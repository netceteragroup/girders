package com.netcetera.girders.demo.showcase.poi;

import com.netcetera.girders.demo.showcase.jdbc.Project;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined.GREY_25_PERCENT;

/**
 * {@code View} for PDFs based on FOP.
 */
@Component
class PoiView extends AbstractView {

  /**
   * Mime type identifier for Excel output.
   */
  public static final String EXCEL_MIME_TYPE = "application/vnd.ms-excel";

  /**
   * Constructor.
   */
  PoiView() {
    setContentType(EXCEL_MIME_TYPE);
  }

  // CHECKSTYLE:OFF

  @SuppressWarnings({"TypeMayBeWeakened", "unchecked"})
  @Override
  protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    // CHECKSTYLE:ON

    List<Project> projects = (List<Project>) model.get("projects");

    // prepare the workbook
    try (HSSFWorkbook workbook = new HSSFWorkbook()) {
      HSSFSheet sheet = workbook.createSheet("Projects");

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
      HSSFCell idLabelCell = headerRow.createCell(0);
      idLabelCell.setCellStyle(headerStyle);
      idLabelCell.setCellValue(new HSSFRichTextString("Identifier"));

      HSSFCell nameLabelCell = headerRow.createCell(1);
      nameLabelCell.setCellStyle(headerStyle);
      nameLabelCell.setCellValue(new HSSFRichTextString("Name"));

      // content rows
      for (int i = 0; i < projects.size(); ++i) {
        HSSFRow row = sheet.createRow(i + 1);
        HSSFCell cell = row.createCell(0);
        cell.setCellStyle(contentStyle);
        cell.setCellValue(new HSSFRichTextString(projects.get(i).getId()));

        cell = row.createCell(1);
        cell.setCellStyle(contentStyle);
        cell.setCellValue(new HSSFRichTextString(projects.get(i).getTitle()));
      }

      response.setContentType(EXCEL_MIME_TYPE);
      workbook.write(response.getOutputStream());
      response.getOutputStream().flush();
    }

  }

}
