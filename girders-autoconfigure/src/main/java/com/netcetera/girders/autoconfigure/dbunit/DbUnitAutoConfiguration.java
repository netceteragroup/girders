package com.netcetera.girders.autoconfigure.dbunit;

import com.netcetera.girders.dbunit.AbstractDbTestCase;
import com.netcetera.girders.dbunit.DbTestCaseConfigurationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Spring Boot auto configuration for the dbunit feature.
 * The auto configuration mechanism works as follows:
 * <ul>
 * <li>if the {@code girders.dbunit.connectionAdapter} property is set, this value is used</li>
 * <li>otherwise, if a H2 database driver is found on the classpath, H2 is configured</li>
 * <li>otherwise, the {@code girders.dbunit.connectionAdapter} property must be set</li>
 * </ul>
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(AbstractDbTestCase.class)
@ConditionalOnMissingBean(value = DbTestCaseConfigurationProperties.class, search = SearchStrategy.CURRENT)
@EnableConfigurationProperties(DbUnitProperties.class)
public class DbUnitAutoConfiguration {

  private static final String H2_CONNECTION_ADAPTER = "org.dbunit.ext.h2.H2Connection";

  private final DbUnitProperties dbUnitProperties;

  /**
   * Constructor.
   *
   * @param dbUnitProperties the dbUnit properties
   */
  public DbUnitAutoConfiguration(DbUnitProperties dbUnitProperties) {
    this.dbUnitProperties = dbUnitProperties;
  }

  /**
   * Bean providing the configuration for dbUnit in case the H2 driver is on the classpath.
   *
   * @return the dbUnit configuration bean
   */
  @Bean
  @ConditionalOnClass(name = "org.h2.Driver")
  public DbTestCaseConfigurationProperties defaultDbTestCaseConfigurationProperties() {
    String effectiveConnectionAdapter = isNotEmpty(dbUnitProperties.getConnectionAdapter()) ? dbUnitProperties
        .getConnectionAdapter() : H2_CONNECTION_ADAPTER;
    logger.info("Setting up dbunit with connectionAdapter '{}' and schema '{}'.", effectiveConnectionAdapter,
        dbUnitProperties.getSchema());
    return new DbTestCaseConfigurationProperties(effectiveConnectionAdapter, dbUnitProperties.getSchema());
  }

  /**
   * Bean providing the configuration for dbUnit in case no H2 driver is on the classpath.
   * In this case, the {@code girders.dbunit.connectionAdapter} property must be set.
   *
   * @return the dbUnit configuration bean
   */
  @Bean
  @ConditionalOnMissingClass("org.h2.Driver")
  public DbTestCaseConfigurationProperties customDbTestCaseConfigurationProperties() {
    if (isEmpty(dbUnitProperties.getConnectionAdapter())) {
      throw new IllegalStateException(
          "No H2 database driver found on classpath, " + "'girders.dbunit.connectionAdapter' must be set manually");
    }

    logger.info("Setting up dbunit with connectionAdapter '{}' and schema '{}'.",
        dbUnitProperties.getConnectionAdapter(), dbUnitProperties.getSchema());
    return new DbTestCaseConfigurationProperties(dbUnitProperties.getConnectionAdapter(), dbUnitProperties.getSchema());

  }
}
