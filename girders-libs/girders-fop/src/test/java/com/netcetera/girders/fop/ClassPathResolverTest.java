package com.netcetera.girders.fop;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests for the FOP template.
 */
class ClassPathResolverTest {

  /**
   * Test.
   */
  @Test
  void testGoodResolve() {
    ClassPathResolver resolver = new ClassPathResolver();

    assertNotNull(resolver.resolve("test-resolve.properties", null));
  }

  /**
   * Test.
   */
  @Test
  void testBadResolve() {
    ClassPathResolver resolver = new ClassPathResolver();

    assertNull(resolver.resolve("classpath:bla.fasel", null));
  }

}
