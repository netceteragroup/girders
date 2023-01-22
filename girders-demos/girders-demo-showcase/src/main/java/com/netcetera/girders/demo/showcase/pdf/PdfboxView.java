package com.netcetera.girders.demo.showcase.pdf;

import com.netcetera.girders.demo.showcase.jdbc.Project;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * {@code View} for PDFs based on PDFbox.
 */
@SuppressWarnings("MethodMayBeStatic")
@Component
public class PdfboxView extends AbstractView {

  /**
   * Constructor.
   */
  PdfboxView() {
    setContentType("application/pdf");
  }

  // CHECKSTYLE:OFF

  @SuppressWarnings("unchecked")
  @Override
  protected void renderMergedOutputModel(Map<String, Object> model,
      HttpServletRequest request, HttpServletResponse response) throws IOException {

    // CHECKSTYLE:ON

    try (PDDocument doc = new PDDocument(); ByteArrayOutputStream stream = createTemporaryOutputStream()) {

      String[][] content = createTable((List<Project>) model.get("projects"));

      PDPage page = new PDPage();
      doc.addPage(page);

      PDPageContentStream contentStream = new PDPageContentStream(doc, page);
      drawTable(page, contentStream, 730.0F, 50.0F, content);
      contentStream.close();

      doc.save(stream);

      writeToResponse(response, stream);
    }
  }

  private String[][] createTable(List<Project> projects) {
    String[][] content = new String[projects.size() + 1][2];
    // header column
    content[0] = new String[]{"Project ID", "Project Name"};
    for (int i = 0; i < projects.size(); i++) {
      Project p = projects.get(i);
      content[i + 1] = new String[]{
          p.getId(), p.getTitle()
      };
    }
    return content;
  }

  private void drawTable(PDPage page, PDPageContentStream contentStream, float y, float margin, String[][] content)
      throws IOException {
    int rows = content.length;
    int cols = content[0].length;
    float rowHeight = 20.0f;
    float tableWidth = page.getCropBox().getWidth() - margin - margin;
    float colWidth = tableWidth / cols;
    float cellMargin = 5.0f;

    // draw the rows
    contentStream.setStrokingColor(Color.lightGray);
    contentStream.setLineWidth(0.4f);
    float nextY = y;
    for (int i = 0; i <= rows; i++) {
      contentStream.moveTo(margin, nextY);
      contentStream.lineTo(margin + tableWidth, nextY);
      contentStream.stroke();
      nextY -= rowHeight;
    }

    // now add the text
    float textX = margin + cellMargin;
    float textY = y - 15.0F;
    for (int i = 0; i < content.length; i++) {
      if (i == 0) {
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12.0F);
      } else {
        contentStream.setFont(PDType1Font.HELVETICA, 12.0F);
      }
      for (int j = 0; j < content[i].length; j++) {
        String text = content[i][j];
        contentStream.beginText();
        contentStream.newLineAtOffset(textX, textY);
        contentStream.showText(text);
        contentStream.endText();
        textX += colWidth;
      }
      textY -= rowHeight;
      textX = margin + cellMargin;
    }
  }

}
