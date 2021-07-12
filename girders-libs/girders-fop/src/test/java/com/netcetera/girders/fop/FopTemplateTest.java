package com.netcetera.girders.fop;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.configuration.Configuration;
import org.apache.fop.configuration.DefaultConfigurationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the FOP template.
 */
class FopTemplateTest {

  private final Resource baseUrl = new ClassPathResource("fop/");

  private final InputStreamSource configLocation = new ClassPathResource("fop/fop.xml");

  private FopTemplate fopTemplate;

  @BeforeEach
  void init() throws Exception {
    FopFactoryBuilder fopFactoryBuilder = new FopFactoryBuilder(baseUrl.getURI());
    fopFactoryBuilder.ignoreNamespace("http://www.w3.org/2001/XMLSchema-instance");
    DefaultConfigurationBuilder cfgBuilder = new DefaultConfigurationBuilder();
    Configuration cfg = cfgBuilder.build(configLocation.getInputStream());
    fopFactoryBuilder.setConfiguration(cfg);
    FopFactory fopFactory = fopFactoryBuilder.build();

    fopTemplate = new FopTemplate(fopFactory, new ClassPathResolver(), null);
  }

  /**
   * Tests conversion from XSL-FO to PDF.
   *
   * @throws FOPException         in case of a conversion error
   * @throws TransformerException in case of a conversion error
   */
  @Test
  void testConvertFo2Pdf() throws Exception {
    Source foSource = new StreamSource(getClass().getResourceAsStream("/fop/sample-fo.xml"));
    ByteArrayOutputStream pdfOutput = new ByteArrayOutputStream();
    fopTemplate.convertFo2Pdf(foSource, pdfOutput);
    assertTrue(pdfOutput.size() > 0);
  }

  /**
   * Tests conversion from XML to PDF using XSLT.
   *
   * @throws FOPException         in case of a conversion error
   * @throws TransformerException in case of a conversion error
   */
  @Test
  void testConvertXml2Pdf() throws Exception {
    Source xmlSource = new StreamSource(getClass().getResourceAsStream("/fop/sample-xhtml.xml"));
    Source xsltSource = new StreamSource(getClass().getResourceAsStream("/fop/xhtml2fo.xsl"));
    ByteArrayOutputStream pdfOutput = new ByteArrayOutputStream();
    // REVIEW 20090120 cd: The operation causes a lookup on www.w3c.org, so
    // the test fails if you are not online. The reason was not thoroughly
    // analyzed, but it's probably a namespace or schema definition in the
    // html file that is used for the test.
    fopTemplate.convertXml2Pdf(xmlSource, xsltSource, pdfOutput);
    assertTrue(pdfOutput.size() > 0);
  }

}
