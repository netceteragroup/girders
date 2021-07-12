package com.netcetera.girders.autoconfigure.csp;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class CspPropertiesTest {

  /**
   * See method name.
   */
  @Test
  void shouldUseHeaderValueAsActualCspDirectivesWhenNoReportUrlIsSet() {
    // given
    String headerValue = "default-src 'self'; img-src 'self' data:";

    CspProperties cspProperties = new CspProperties();
    cspProperties.setHeaderValue(headerValue);
    cspProperties.setReportUrl(null);

    // when
    String cspDirectives = cspProperties.deriveActualCspDirectives("/unused-root");

    // then
    assertThat(cspDirectives, is(headerValue));
  }

  /**
   * See method name.
   */
  @Test
  void shouldAppendAReportUriCspDirectiveToTheHeaderValueWhenAReportUrlIsSet() {
    // given
    String headerValue = "default-src 'self'";
    String reportUrl = "csp-report";

    CspProperties cspProperties = new CspProperties();
    cspProperties.setHeaderValue(headerValue);
    cspProperties.setReportUrl(reportUrl);

    // when
    String cspDirectives = cspProperties.deriveActualCspDirectives("/unused-root");

    // then
    assertThat(cspDirectives, is(headerValue + "; report-uri " + reportUrl));
  }

  /**
   * See method name.
   */
  @Test
  void
  shouldAppendAReportUriCspDirectiveToTheHeaderValueWhenAReportUrlIsSetAndPrependTheServletContextPathToTheAbsoluteReportUrl() {
    // given
    String headerValue = "default-src 'self'";
    String reportUrl = "/csp-report";
    String servletContextPath = "/root";

    CspProperties cspProperties = new CspProperties();
    cspProperties.setHeaderValue(headerValue);
    cspProperties.setReportUrl(reportUrl);

    // when
    String cspDirectives = cspProperties.deriveActualCspDirectives(servletContextPath);

    // then
    assertThat(cspDirectives, is(headerValue + "; report-uri " + servletContextPath + reportUrl));
  }

}
