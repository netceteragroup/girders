package com.netcetera.girders.nca;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests.
 */
class NcaUtilsTest {

  /**
   * Tests for findNcaProjectId.
   */
  @Test
  void testFindNcaProjectIdForSearchString() {
    assertThat(NcaUtils.findProjectId("esa-019-0"), is("esa-019-0"));
    assertThat(NcaUtils.findProjectId("esa-019"), is("esa-019-0"));
    assertThat(NcaUtils.findProjectId("esa-019-"), is("esa-019-0"));
    assertThat(NcaUtils.findProjectId(" esa-019-0  "), is("esa-019-0"));
    assertThat(NcaUtils.findProjectId(" esa-019  "), is("esa-019-0"));
    assertThat(NcaUtils.findProjectId("foo esa-019-0 \t bar"), is("esa-019-0"));
    assertThat(NcaUtils.findProjectId("foo esa-019-  bar"), is("esa-019-0"));
    assertThat(NcaUtils.findProjectId(" fooesa-019bar  "), is("esa-019-0"));

    assertThat(NcaUtils.findProjectId("esa-019-1"), is(nullValue()));
    assertThat(NcaUtils.findProjectId("es�-019-"), is(nullValue()));
    assertThat(NcaUtils.findProjectId("esa019"), is(nullValue()));
    assertThat(NcaUtils.findProjectId("esa019-0"), is(nullValue()));
    assertThat(NcaUtils.findProjectId("foobar"), is(nullValue()));
    assertThat(NcaUtils.findProjectId(""), is(nullValue()));
    assertThat(NcaUtils.findProjectId(null), is(nullValue()));
  }

  /**
   * Tests for isValidNcaProjectId.
   */
  @Test
  void testIsValidNcaProjectId() {
    assertThat(NcaUtils.isValidProjectId("esa-019-0"), is(true));
    assertThat(NcaUtils.isValidProjectId("EsA-019-0"), is(true));
    assertThat(NcaUtils.isValidProjectId("NCA-026-9"), is(true));

    assertThat(NcaUtils.isValidProjectId(" esa-019-0 "), is(false));
    assertThat(NcaUtils.isValidProjectId("esa-019"), is(false));
    assertThat(NcaUtils.isValidProjectId("esa-019-"), is(false));
    assertThat(NcaUtils.isValidProjectId("esa-019-1"), is(false));
    assertThat(NcaUtils.isValidProjectId("foobar"), is(false));
    assertThat(NcaUtils.isValidProjectId(""), is(false));
    assertThat(NcaUtils.isValidProjectId(null), is(false));
  }

  /**
   * See method name.
   */
  @Test
  void shouldThrowNPEBecauseNumberIsMissing() {
    assertThrows(NullPointerException.class, () -> NcaUtils.completeProjectId("nca", null));
  }

  /**
   * See method name.
   */
  @Test
  void shouldThrowNPEBecauseTagIsMissing() {
    assertThrows(NullPointerException.class, () -> NcaUtils.completeProjectId(null, "177"));
  }

  /**
   * See method name.
   */
  @Test
  void shouldThrowNPEBecauseNumberIsMissingForChecksum() {
    assertThrows(NullPointerException.class, () -> NcaUtils.calculateProjectIdChecksum("esa", null));
  }

  /**
   * See method name.
   */
  @Test
  void shouldThrowNPEBecauseTagIsMissingForChecksum() {
    assertThrows(NullPointerException.class, () -> NcaUtils.calculateProjectIdChecksum(null, "019"));
  }

  /**
   * See method name.
   */
  @Test
  void shouldThrowIAEBecauseOfSpaces() {
    assertThrows(IllegalArgumentException.class, () -> NcaUtils.completeProjectId(" nca", "123"));
  }

  /**
   * See method name.
   */
  @Test
  void shouldThrowIAEBecauseTagTooLong() {
    assertThrows(IllegalArgumentException.class, () -> NcaUtils.completeProjectId("nnca", "123"));
  }

  /**
   * See method name.
   */
  @Test
  void shouldThrowIAEBecauseTagTooShort() {
    assertThrows(IllegalArgumentException.class, () -> NcaUtils.completeProjectId("na", "123"));
  }

  /**
   * See method name.
   */
  @Test
  void shouldThrowIAEBecauseTagContainsNumber() {
    assertThrows(IllegalArgumentException.class, () -> NcaUtils.completeProjectId("n1a", "123"));
  }

  /**
   * See method name.
   */
  @Test
  void shouldThrowIAEBecauseTagIsEmpty() {
    assertThrows(IllegalArgumentException.class, () -> NcaUtils.completeProjectId("", "123"));
  }

  /**
   * See method name.
   */
  @Test
  void shouldThrowIAEBecauseNumberIsTooLong() {
    assertThrows(IllegalArgumentException.class, () -> NcaUtils.completeProjectId("nca", "1234"));
  }

  /**
   * See method name.
   */
  @Test
  void shouldThrowIAEBecauseNumberIsEmpty() {
    assertThrows(IllegalArgumentException.class, () -> NcaUtils.completeProjectId("nca", ""));
  }

  /**
   * See method name.
   */
  @Test
  void shouldThrowIAEBecauseNumberContainsLetter() {
    assertThrows(IllegalArgumentException.class, () -> NcaUtils.completeProjectId("nca", "1a3"));
  }

  /**
   * Test valid project ids.
   */
  @Test
  void testValidProjectIds() {
    assertThat(NcaUtils.completeProjectId("esa", "019"), is("esa-019-0"));
    assertThat(NcaUtils.completeProjectId("EsA", "019"), is("EsA-019-0"));
    assertThat(NcaUtils.completeProjectId("ESA", "023"), is("ESA-023-3"));
    assertThat(NcaUtils.completeProjectId("UBS", "069"), is("UBS-069-2"));
    assertThat(NcaUtils.completeProjectId("UBS", "117"), is("UBS-117-2"));
    assertThat(NcaUtils.completeProjectId("NCA", "026"), is("NCA-026-9"));
    assertThat(NcaUtils.completeProjectId("NCA", "163"), is("NCA-163-0"));
  }

  /**
   * Test valid checksums.
   */
  @Test
  void testValidProjectIdChecksums() {
    assertThat(NcaUtils.calculateProjectIdChecksum("esa", "019"), is("0"));
    assertThat(NcaUtils.calculateProjectIdChecksum("EsA", "019"), is("0"));
    assertThat(NcaUtils.calculateProjectIdChecksum("ESA", "023"), is("3"));
    assertThat(NcaUtils.calculateProjectIdChecksum("UBS", "069"), is("2"));
    assertThat(NcaUtils.calculateProjectIdChecksum("UBS", "117"), is("2"));
    assertThat(NcaUtils.calculateProjectIdChecksum("NCA", "026"), is("9"));
    assertThat(NcaUtils.calculateProjectIdChecksum("NCA", "163"), is("0"));
  }

  /**
   * Bulk test.
   *
   * @throws IOException in case an I/O error occurs
   */
  @Test
  void bulkTestProjectIdMethods() throws IOException {
    for (String projectId : getProjectIds()) {
      String[] parts = projectId.split("-");

      String calculatedProjectId = NcaUtils.completeProjectId(parts[0], parts[1]);
      assertThat(projectId + " (Calculated project id: " + calculatedProjectId + ") not valid!", calculatedProjectId,
          is(projectId));
      assertThat(NcaUtils.isValidProjectId(projectId), is(true));
      assertThat(NcaUtils.calculateProjectIdChecksum(parts[0], parts[1]), is(parts[2]));
      assertThat(NcaUtils.findProjectId(projectId), is(projectId));
      assertThat(NcaUtils.findProjectId(parts[0] + '-' + parts[1]), is(projectId));
    }
  }

  private static Iterable<String> getProjectIds() throws IOException {
    try (InputStream resourceStream = new ClassPathResource("project-ids.properties").getInputStream()) {
      Properties projectIds = new Properties();
      projectIds.load(resourceStream);
      return projectIds.stringPropertyNames();
    }
  }

  /**
   * See method name.
   */
  @Test
  void shouldCreateInfostoreStampWithUsernameFirst() {
    // given
    String unixname = "jcyriac";
    // when
    String stamp = NcaUtils.createInfostoreStamp(unixname);
    // then
    assertThat(stamp.startsWith(unixname), is(true));
  }

  /**
   * See method name.
   */
  @Test
  void shouldThrowExceptionIfUsernameIsEmpty() {
    assertThrows(IllegalArgumentException.class, () -> {
      // given
      String unixname = "";
      // when
      NcaUtils.createInfostoreStamp(unixname);
    });
  }

  /**
   * See method name.
   */
  @Test
  void shouldThrowExceptionIfUsernameIsNull() {
    assertThrows(IllegalArgumentException.class, () -> NcaUtils.createInfostoreStamp(null));
  }

}
