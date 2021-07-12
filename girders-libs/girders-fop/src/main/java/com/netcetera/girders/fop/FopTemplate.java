package com.netcetera.girders.fop;

import com.google.common.collect.Sets;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.xmlgraphics.util.MimeConstants;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXResult;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

/**
 * Provides convenience methods for working with Apache FOP.
 */
@RequiredArgsConstructor
@Slf4j
public class FopTemplate {

  private final FopFactory fopFactory;

  private final URIResolver uriResolver;

  private final MeterRegistry meterRegistry;

  /**
   * Converts a FO source to PDF and writes the result to the given output
   * stream.
   *
   * @param foSource  the FO source
   * @param pdfOutput the outputStream to write the PDF to. For performance
   *                  reasons you should use a {@link java.io.BufferedOutputStream}
   *
   * @throws FOPException         In case of a FOP problem
   * @throws TransformerException In case of a transformation problem
   */
  public void convertFo2Pdf(Source foSource, OutputStream pdfOutput)
      throws FOPException, TransformerException {
    long start = System.currentTimeMillis();

    // Construct fop with desired output format
    Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, pdfOutput);

    // Setup JAXP using identity transformer
    TransformerFactory factory = TransformerFactory.newInstance();
    if (uriResolver != null) {
      factory.setURIResolver(uriResolver);
    }
    Transformer transformer = factory.newTransformer();

    // Resulting SAX events (the generated FO) must be piped through to FOP
    Result res = new SAXResult(fop.getDefaultHandler());

    // Start XSLT transformation and FOP processing
    logger.debug("Generating new PDF into output stream");
    transformer.transform(foSource, res);

    long end = System.currentTimeMillis();

    if (meterRegistry != null) {
      meterRegistry.timer("girders.fop.convertFoToPdf", Sets.newHashSet()).record(end - start, TimeUnit.MILLISECONDS);
    }
  }

  /**
   * Converts a XML source to PDF using XSLT and FOP and writes the result to
   * the given output stream.
   *
   * @param xmlSource  the XML source
   * @param xsltSource the XSLT source
   * @param pdfOutput  the outputStream to write the PDF to. For performance
   *                   reasons you should use a {@link java.io.BufferedOutputStream}
   *
   * @throws FOPException         In case of a FOP problem
   * @throws TransformerException In case of a transformation problem
   */
  public void convertXml2Pdf(Source xmlSource, Source xsltSource, OutputStream pdfOutput)
      throws FOPException, TransformerException {
    long start = System.currentTimeMillis();

    // Construct fop with desired output format
    Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, pdfOutput);

    // Setup XSLT
    TransformerFactory factory = TransformerFactory.newInstance();
    if (uriResolver != null) {
      factory.setURIResolver(uriResolver);
    }
    Transformer transformer = factory.newTransformer(xsltSource);

    // Resulting SAX events (the generated FO) must be piped through to FOP
    Result res = new SAXResult(fop.getDefaultHandler());

    // Start XSLT transformation and FOP processing
    logger.debug("Generating new PDF from XML into output stream");
    transformer.transform(xmlSource, res);

    long end = System.currentTimeMillis();

    if (meterRegistry != null) {
      meterRegistry.timer("girders.fop.convertXml2Pdf", Sets.newHashSet()).record(end - start, TimeUnit.MILLISECONDS);
    }
  }

}
