package com.netcetera.girders.resttemplatelogging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Tests for the {@link LoggingClientHttpRequestInterceptor} class.
 */
public class LoggingClientHttpRequestInterceptorTest {

  private static final String HOST = "foo.bar";
  private static final String ROOT_URI = "http://" + HOST;
  private static final String PATH = "/hotels/42";

  private static final String REQUEST_LOG_MESSAGE = "request log message";
  private static final String RESPONSE_LOG_MESSAGE = "response log message";

  private RestTemplate restTemplate;

  /**
   * Setup.
   */
  @BeforeEach
  void setup() {
    RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
    restTemplate = restTemplateBuilder.rootUri(ROOT_URI).additionalInterceptors(
        new LoggingClientHttpRequestInterceptor(new TestLogFormatter())).build();
  }

  /**
   * See method name.
   */
  @Test
  void shouldLogRequestAndResponse() {

    // given
    runMockHotel42Server();
    RecordingAppender appender = addAppender();

    // when
    executeHotel42GetRequest();

    // then
    assertThat(appender.loggedMessageCount(), is(equalTo(2)));
    assertThat(appender.getLoggingEvent(0).getLoggerName(), is(equalTo("ClientHttpRequest." + HOST)));
    assertThat(appender.getLoggingEvent(1).getLoggerName(), is(equalTo("ClientHttpRequest." + HOST)));
    assertThat(appender.getLoggedMessage(0), is(equalTo(REQUEST_LOG_MESSAGE)));
    assertThat(appender.getLoggedMessage(1), is(equalTo(RESPONSE_LOG_MESSAGE)));
  }

  private void runMockHotel42Server(HttpMethod method) {
    MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
    mockServer.expect(requestTo(ROOT_URI + PATH)).andExpect(method(method)).andRespond(
        withSuccess("{ \"id\" : \"42\", \"name\" : \"Holiday Inn\"}", MediaType.APPLICATION_JSON));
  }

  private void runMockHotel42Server() {
    runMockHotel42Server(HttpMethod.GET);
  }

  private void executeHotel42GetRequest() {
    restTemplate.getForObject(PATH, String.class);
  }

  private static RecordingAppender addAppender() {
    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

    RecordingAppender appender = new RecordingAppender();
    appender.setContext(context);

    Logger logger = context.getLogger("ClientHttpRequest");
    logger.setAdditive(true);
    logger.addAppender(appender);
    logger.setLevel(Level.TRACE);

    appender.start();

    return appender;
  }

  private static class RecordingAppender extends AppenderBase<ILoggingEvent> {

    private final List<ILoggingEvent> loggingEvents = new ArrayList<>();

    @Override
    protected void append(ILoggingEvent loggingEvent) {
      loggingEvents.add(loggingEvent);
    }

    int loggedMessageCount() {
      return loggingEvents.size();
    }

    ILoggingEvent getLoggingEvent(int i) {
      return loggingEvents.get(i);
    }

    String getLoggedMessage(int i) {
      return loggingEvents.get(i).getMessage();
    }

  }

  private static class TestLogFormatter implements LogFormatter {

    @Override
    public String format(String loggingId, HttpRequest request, byte[] body) {
      return REQUEST_LOG_MESSAGE;
    }

    @Override
    public String format(String loggingId, ClientHttpResponse response) {
      return RESPONSE_LOG_MESSAGE;
    }
  }

}
