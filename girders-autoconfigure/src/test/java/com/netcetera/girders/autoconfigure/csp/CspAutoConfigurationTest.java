package com.netcetera.girders.autoconfigure.csp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ContentSecurityPolicyHeaderWriter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

/**
 * Tests for the {@link CspAutoConfiguration} class.
 */
@SpringBootTest(
    properties = {"girders.csp.report-url=/csp/report", "girders.csp.enableDefaultCspViolationReportServlet = true"})
@AutoConfigureMockMvc
@ImportAutoConfiguration(CspAutoConfiguration.class)
@ExtendWith(SpringExtension.class)
class CspAutoConfigurationTest {

  private static final String EXPECTED_CSP_HEADER_VALUE = "default-src 'self'; report-uri /csp/report";

  @Autowired
  private MockMvc mockMvc;

  /**
   * See method name.
   *
   * @throws Exception in case of an error
   */
  @Test
  void shouldProvideAAutoConfiguredCspHeaderWriter() throws Exception {
    // given Spring Boot auto configuration

    // when
    ResultActions resultActions = mockMvc.perform(get("/any-url"));

    // then
    resultActions.andExpect(header().stringValues("Content-Security-Policy", EXPECTED_CSP_HEADER_VALUE));
  }

  @Configuration
  static class TestConfiguration  {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private ContentSecurityPolicyHeaderWriter cspHeaderWriter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      return http.headers().addHeaderWriter(cspHeaderWriter).and().build();
    }
  }

}
