package com.netcetera.girders.autoconfigure.dbunit;

import com.netcetera.girders.dbunit.DbTestCaseConfigurationProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/**
 * Tests for the {@link DbUnitAutoConfiguration} class.
 */
@TestPropertySource(properties = {"girders.dbunit.connectionAdapter=foo", "girders.dbunit.schema=bar"})
@ImportAutoConfiguration(DbUnitAutoConfiguration.class)
@ExtendWith(SpringExtension.class)
class DbUnitAutoConfigurationTest {

  @Autowired
  private DbTestCaseConfigurationProperties dbTestCaseConfigurationProperties;

  @Test
  void shouldProvideConfiguration() {
    assertThat(dbTestCaseConfigurationProperties, is(not(nullValue())));
    assertThat(dbTestCaseConfigurationProperties.getConnectionAdapter(), is("foo"));
    assertThat(dbTestCaseConfigurationProperties.getSchema(), is("bar"));
  }

  @Test
  void shouldProvideDefaultConfiguration() {
    DbUnitProperties dbUnitProperties = new DbUnitProperties();
    DbUnitAutoConfiguration config = new DbUnitAutoConfiguration(dbUnitProperties);

    DbTestCaseConfigurationProperties props = config.defaultDbTestCaseConfigurationProperties();

    assertThat(props, is(notNullValue()));
  }

}
