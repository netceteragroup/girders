package com.netcetera.girders.resttemplatelogging;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.util.StreamUtils.copyToString;

/**
 * Tests for the {@link JsonLogFormatter} class.
 */
class JsonLogFormatterTest {

  private static final String EXPECTED_REQUEST_JSON_RESOURCE_PATH
      = "JsonLogFormatterTest-expected-request-log-message.json";
  private static final String EXPECTED_RESPONSE_JSON_RESOURCE_PATH
      = "JsonLogFormatterTest-expected-response-log-message.json";

  private static final String CORRELATION_ID = "id-123";
  private static final String REQUEST_URI = "http://foo.bar/hotels/42";
  private static final String RESPONSE_BODY = "{ \"id\" : \"42\", \"name\" : \"Holiday Inn\"}";

  private final LogFormatter logFormatter = new JsonLogFormatter();

  private static MockClientHttpResponse mockResponse(HttpStatus status, String body) {
    return new MockClientHttpResponse(body.getBytes(StandardCharsets.UTF_8), status);
  }

  /**
   * See method name.
   */
  @Test
  void shouldFormatRequestAsExpected() throws Exception {
    // given
    MockClientHttpRequest request = mockRequest(GET, REQUEST_URI);
    request.getHeaders().add("Accept", "text/plain");
    request.getHeaders().add("Accept", "application/json");
    request.getHeaders().add("X-Test-Header", "test-value");
    String body = "";

    // when
    String logMessage = logFormatter.format(CORRELATION_ID, request, body.getBytes(StandardCharsets.UTF_8));

    // then
    assertThatIsSingleLineLogMessage(logMessage);

    String expectedJsonContent = readJsonClasspathResource(EXPECTED_REQUEST_JSON_RESOURCE_PATH);
    JSONAssert.assertEquals(expectedJsonContent, logMessage, STRICT);
  }

  private String readJsonClasspathResource(String path) throws IOException {
    URL resource = getClass().getClassLoader().getResource(path);
    try (InputStream inputStream = resource.openStream()) {
      return copyToString(inputStream, StandardCharsets.UTF_8);
    }
  }

  /**
   * See method name.
   */
  @Test
  void shouldFormatResponseAsExpected() throws Exception {
    // given
    MockClientHttpResponse response = mockResponse(OK, RESPONSE_BODY);
    response.getHeaders().add("Content-Type", "application/json");
    response.getHeaders().add("X-Test-Header", "test-value");

    // when
    String logMessage = logFormatter.format(CORRELATION_ID, response);

    // then
    assertThatIsSingleLineLogMessage(logMessage);

    String expectedJsonContent = readJsonClasspathResource(EXPECTED_RESPONSE_JSON_RESOURCE_PATH);
    JSONAssert.assertEquals(expectedJsonContent, logMessage, STRICT);
  }

  private static MockClientHttpRequest mockRequest(HttpMethod method, String uri) throws URISyntaxException {
    MockClientHttpRequest request = new MockClientHttpRequest();
    request.setMethod(method);
    request.setURI(new URI(uri));
    return request;
  }

  private static void assertThatIsSingleLineLogMessage(String logMessage) {
    assertThat(logMessage, not(containsString("\n")));
    assertThat(logMessage, not(containsString("\r")));
  }

}
