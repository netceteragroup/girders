package com.netcetera.girders.demo.showcase.pdf;

import com.netcetera.girders.fop.FopTemplate;
import org.apache.fop.apps.FOPException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

/**
 * {@code View} for PDFs based on FOP.
 */
@Component
class FopView extends AbstractView {

  private final FopTemplate fopTemplate;

  private final TemplateEngine templateEngine;

  /**
   * Constructor.
   *
   * @param fopTemplate    Template object for executing fop operations.
   * @param templateEngine Thymeleaf template engine
   */
  FopView(FopTemplate fopTemplate, TemplateEngine templateEngine) {
    setContentType("application/pdf");

    this.fopTemplate = fopTemplate;
    this.templateEngine = templateEngine;
  }

  @Override
  protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
      HttpServletResponse response) throws FOPException, IOException, TransformerException {
    String result = templateEngine.process("pdf-fop.xml", new Context(request.getLocale(), model));
    response.setContentType(getContentType());
    Source foSource = new StreamSource(new StringReader(result));
    fopTemplate.convertFo2Pdf(foSource, response.getOutputStream());
  }

}
