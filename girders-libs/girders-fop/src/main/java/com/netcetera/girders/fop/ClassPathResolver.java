package com.netcetera.girders.fop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;

/**
 * Classpath-Resolver. With this, you can easily use sub-templates in your
 * fop-file.
 */
@Slf4j
public class ClassPathResolver implements URIResolver {

  @Override
  public Source resolve(String href, String base) {

    logger.debug("resolving href:{} base:{}", new Object[]{href, base});

    try {
      return new StreamSource(new ClassPathResource(href).getInputStream());
    } catch (IOException e) {
      logger.error("Problems while searching for {}", href, e);
      return null;
    }

  }


}
