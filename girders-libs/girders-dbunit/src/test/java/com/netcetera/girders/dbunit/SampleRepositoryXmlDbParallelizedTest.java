package com.netcetera.girders.dbunit;

import com.netcetera.girders.dbunit.dataset.DataSetProvider;
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
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.NoResultException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for the {@link SampleRepository}.
 */
@EnableAutoConfiguration
@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class SampleRepositoryXmlDbParallelizedTest extends AbstractParallelizedXmlDbTestCase {

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
   * Tests whether {@link DataSetProvider#getDbUnitUpdateDataSet()} gets loaded.
   */
  @Test
  void testUpdateDataSet() {

    // setup
    String sampleName = "bazar";

    // exercise
    Sample sample = sampleRepository.findSampleByName(sampleName);

    // verify
    assertThat(sample, Matchers.is(Matchers.notNullValue()));
    assertThat(sample.getName(), Matchers.is(Matchers.equalTo(sampleName)));
    assertThat(sample.getId(), Matchers.is(3L));
    assertThat(sample.getReferences(), Matchers.hasSize(1));

    Reference ref = sample.getReferences().get(0);
    assertThat(ref.getId(), Matchers.is(1000L));
    assertThat(ref.getSample(), Matchers.is(sample));
    assertThat(ref.getSamples(), Matchers.contains(sample));
    assertThat(sample.getReference(), Matchers.is(ref));
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
