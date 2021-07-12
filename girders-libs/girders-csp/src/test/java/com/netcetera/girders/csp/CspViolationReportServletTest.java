package com.netcetera.girders.csp;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for the {@link CspViolationReportServlet} class.
 */
class CspViolationReportServletTest {

  @Test
  void shouldProcessPostRequests() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setContent("Test Content".getBytes("UTF-8"));
    MockHttpServletResponse response = new MockHttpServletResponse();

    CspViolationReportServlet servlet = new CspViolationReportServlet();

    servlet.doPost(request, response);

    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

}