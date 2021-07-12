package com.netcetera.girders.resttemplatelogging;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

/**
 * Tests for the {@link PrettyPrintLogFormatter} class.
 */
class PrettyPrintLogFormatterTest {

  private static final String CORRELATION_ID = "id-123";
  private static final String REQUEST_URI = "http://foo.bar/hotels/42";
  private static final String RESPONSE_BODY = "{ \"id\" : \"42\", \"name\" : \"Holiday Inn\"}";

  private final PrettyPrintLogFormatter logFormatter = new PrettyPrintLogFormatter();

  private static void assertHotel42ResponseLog(String logMessage) {
    /*-
     * ID: id-123 / Response:
     * 8<-------------------- vvv
     * StatusCode: 200
     *
     * Headers
     * -------
     * Content-Type: application/json
     * X-Test-Header: test-value
     *
     * Body
     * ----
     * { "id" : "42", "name" : "Holiday Inn"}
     * 8<-------------------- ^^^
     */
    assertThat(logMessage, containsString("8<-------------------- vvv"));
    assertThat(logMessage, containsString("ID: id-123 / Response:"));
    assertThat(logMessage, containsString("StatusCode: 200"));
    assertThat(logMessage, containsString("Content-Type: application/json"));
    assertThat(logMessage, containsString("Body"));
    assertThat(logMessage, containsString("{ \"id\" : \"42\", \"name\" : \"Holiday Inn\"}"));
    assertThat(logMessage, containsString("8<-------------------- ^^^"));
  }

  /**
   * See method name.
   */
  @Test
  void shouldFormatResponseAsExpected() throws IOException {
    // given
    MockClientHttpResponse response = mockResponse(OK, RESPONSE_BODY);
    response.getHeaders().add("Content-Type", "application/json");
    response.getHeaders().add("X-Test-Header", "test-value");

    // when
    String logMessage = logFormatter.format(CORRELATION_ID, response);

    // then
    assertHotel42ResponseLog(logMessage);
  }

  private static MockClientHttpResponse mockResponse(HttpStatus status, String body) {
    return new MockClientHttpResponse(body.getBytes(StandardCharsets.UTF_8), status);
  }

  /**
   * See method name.
   */
  @Test
  void shouldFormatRequestAsExpected() throws URISyntaxException {
    // given
    MockClientHttpRequest request = mockRequest(GET, REQUEST_URI);
    request.getHeaders().add("Accept", "text/plain");
    request.getHeaders().add("Accept", "application/json");
    request.getHeaders().add("X-Test-Header", "test-value");
    String body = "";

    // when
    String logMessage = logFormatter.format(CORRELATION_ID, request, body.getBytes(StandardCharsets.UTF_8));

    // then
    assertHotel42RequestLog(logMessage);
  }

  private static MockClientHttpRequest mockRequest(HttpMethod method, String uri) throws URISyntaxException {
    MockClientHttpRequest request = new MockClientHttpRequest();
    request.setMethod(method);
    request.setURI(new URI(uri));
    return request;
  }

  private static void assertHotel42RequestLog(String logMessage) {
    /*-
     * ID: id-123 / Request:
     * 8<-------------------- vvv
     * URI: http://foo.bar/hotels/42
     * Method: GET
     *
     * Headers
     * -------
     * Accept: text/plain, application/json
     * X-Test-Header: test-value
     *
     * 8<-------------------- ^^^
     */
    assertThat(logMessage, containsString("ID: id-123 / Request:"));
    assertThat(logMessage, containsString("8<-------------------- vvv"));
    assertThat(logMessage, containsString("Method: GET"));
    assertThat(logMessage, containsString("URI: http://foo.bar/hotels/42"));
    assertThat(logMessage, containsString("Accept: text/plain, application/json"));
    assertThat(logMessage, not(containsString("Body")));
    assertThat(logMessage, containsString("8<-------------------- ^^^"));
  }

}
