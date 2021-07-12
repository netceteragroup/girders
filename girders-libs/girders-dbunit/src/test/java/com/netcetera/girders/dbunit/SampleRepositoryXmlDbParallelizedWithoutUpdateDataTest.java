package com.netcetera.girders.dbunit;

import com.netcetera.girders.dbunit.datamanagement.ParallelizedDataManagementProvider;
import com.netcetera.girders.dbunit.dataset.FlatXmlDataSetProvider;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests that {@link ParallelizedDataManagementProvider} works for FlatXML based tests if there is no
 * {@link FlatXmlDataSetProvider#getDbUnitUpdateDataSet() update data set}.
 */
@EnableAutoConfiguration
@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class SampleRepositoryXmlDbParallelizedWithoutUpdateDataTest extends AbstractParallelizedXmlDbTestCase {

  /**
   * Configuration for the test.
   */
  @Configuration
  @ComponentScan("com.netcetera.girders")
  static class TestConfiguration {
  }

  @Autowired
  private SampleRepository sampleRepository;

  /**
   * Tests whether the data set gets loaded if no
   * {@link FlatXmlDataSetProvider#getDbUnitUpdateDataSet() update data set} exists by testing the
   * {@link SampleRepository#findSampleByName(String)} method.
   */
  @Test
  void testFindSampleByNameReturnsExisting() {

    // setup
    String sampleName = "super";

    // exercise
    Sample sample = sampleRepository.findSampleByName(sampleName);

    // verify
    assertThat(sample, Matchers.is(Matchers.notNullValue()));
    assertThat(sample.getName(), Matchers.is(Matchers.equalTo(sampleName)));
  }
}
