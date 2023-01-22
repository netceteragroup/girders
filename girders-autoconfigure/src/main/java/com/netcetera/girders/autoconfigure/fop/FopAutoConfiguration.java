package com.netcetera.girders.autoconfigure.fop;

import com.netcetera.girders.fop.ClassPathResolver;
import com.netcetera.girders.fop.FopTemplate;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.configuration.ConfigurationException;
import org.apache.fop.configuration.DefaultConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import javax.xml.transform.URIResolver;
import java.io.IOException;

/**
 * Spring Boot auto configuration for the I18N feature.
 */
@AutoConfiguration
@ConditionalOnClass(FopTemplate.class)
@EnableConfigurationProperties(FopProperties.class)
public class FopAutoConfiguration {

  private final FopProperties properties;

  private final MeterRegistry meterRegistry;

  /**
   * Constructor.
   *
   * @param properties    Properties for the FOP feature
   * @param meterRegistry Metering registry (optional)
   */
  public FopAutoConfiguration(FopProperties properties, @Autowired(required = false) MeterRegistry meterRegistry) {
    this.properties = properties;
    this.meterRegistry = meterRegistry;
  }

  /**
   * Create an instance of the FOP template.
   *
   * @param fopFactory  FOP factory
   * @param uriResolver URI resolver
   *
   * @return Template
   */
  @ConditionalOnMissingBean(FopTemplate.class)
  @Bean
  public FopTemplate fopTemplate(FopFactory fopFactory, URIResolver uriResolver) {
    return new FopTemplate(fopFactory, uriResolver, meterRegistry);
  }

  /**
   * Create an instance of a URI resolver.
   *
   * @return URI resolver
   */
  @ConditionalOnMissingBean(name = "fopUriResolver")
  @Bean
  public URIResolver fopUriResolver() {
    return new ClassPathResolver();
  }

  /**
   * Create an instance of a FOP factory.
   *
   * @return FOP factory instance
   *
   * @throws IOException            if the initialisation failed
   * @throws ConfigurationException if the processing of the configuration
   *                                failed
   */
  @ConditionalOnMissingBean(FopFactory.class)
  @Bean
  public FopFactory fopFactory() throws IOException, ConfigurationException {
    FopFactoryBuilder fopFactoryBuilder = new FopFactoryBuilder(properties.getBase());
    fopFactoryBuilder.ignoreNamespace("http://www.w3.org/2001/XMLSchema-instance");
    fopFactoryBuilder.setConfiguration(
        new DefaultConfigurationBuilder().build(properties.getConfig().getInputStream()));
    return fopFactoryBuilder.build();
  }

}
