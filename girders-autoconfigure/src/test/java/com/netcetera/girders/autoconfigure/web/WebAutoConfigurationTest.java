package com.netcetera.girders.autoconfigure.web;

import com.netcetera.girders.web.JSessionIdFilter;
import com.netcetera.girders.web.ServletExceptionLoggingFilter;
import com.netcetera.girders.web.WebMappedDiagnosticContextFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = "girders.web.servlet-exception-logging=true", classes = WebAutoConfiguration.class)
@ExtendWith(SpringExtension.class)
class WebAutoConfigurationTest {

  @Autowired
  private WebMappedDiagnosticContextFilter webMappedDiagnosticContextFilter;

  @Autowired
  private ServletExceptionLoggingFilter servletExceptionLoggingFilter;

  @Autowired
  private JSessionIdFilter jSessionIdFilter;

  @Test
  void mappedDiagnosticContextFilter() {
    assertNotNull(webMappedDiagnosticContextFilter);
  }

  @Test
  void servletExceptionLoggingFilter() {
    assertNotNull(servletExceptionLoggingFilter);
  }

  @Test
  void jSessionIdFilter() {
    assertNotNull(jSessionIdFilter);
  }

}