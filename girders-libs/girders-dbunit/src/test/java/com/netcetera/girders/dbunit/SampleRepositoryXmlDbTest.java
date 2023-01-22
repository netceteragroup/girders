package com.netcetera.girders.dbunit;

import jakarta.persistence.NoResultException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.support.TransactionTemplate;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.parallel.ExecutionMode.SAME_THREAD;

/**
 * Tests for the {@link SampleRepository}.
 */
@EnableAutoConfiguration
@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@Execution(SAME_THREAD)
public class SampleRepositoryXmlDbTest extends AbstractXmlDbTestCase {

  /**
   * Configuration for the test.
   */
  @Configuration
  @ComponentScan("com.netcetera.girders")
  static class TestConfiguration {

    @Bean
    DbTestCaseConfigurationProperties configurationProperties() {
      return new DbTestCaseConfigurationProperties("org.dbunit.ext.h2.H2Connection", null);
    }
  }

  @Autowired
  private SampleRepository sampleRepository;

  /**
   * Tests the {@link SampleRepository#findSampleByName(String)} method.
   */
  @Test
  void testFindSampleByNameReturnsExisting() {

    // setup
    String sampleName = "foo";

    // exercise
    Sample sample = sampleRepository.findSampleByName(sampleName);

    // verify
    assertThat(sample, Matchers.is(Matchers.notNullValue()));
    assertThat(sample.getName(), Matchers.is(Matchers.equalTo(sampleName)));
  }

  /**
   * Tests the {@link SampleRepository#findSampleByName(String)} method.
   */
  @Test
  void testFindSampleByNameThrowsExceptionForNonExisting() {

    // setup
    String sampleName = "non-existing";

    // exercise + verify
    assertThrows(NoResultException.class, () -> sampleRepository.findSampleByName(sampleName));
  }

  /**
   * Test the creation of a {@link TransactionTemplate}.
   */
  @Test
  void testCreateTransactionTemplate() {
    TransactionTemplate transactionTemplate = createTransactionTemplate();
    assertThat(transactionTemplate, Matchers.is(Matchers.not(Matchers.nullValue())));
  }

}
