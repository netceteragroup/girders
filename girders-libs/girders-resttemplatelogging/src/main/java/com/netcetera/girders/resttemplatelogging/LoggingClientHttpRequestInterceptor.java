package com.netcetera.girders.resttemplatelogging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.UUID;

/**
 * {@link ClientHttpRequestInterceptor} that logs both request and response.
 */
public class LoggingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

  private final LogFormatter logFormatter;

  /**
   * Constructor.
   *
   * @param logFormatter The log formatter to use.
   */
  public LoggingClientHttpRequestInterceptor(LogFormatter logFormatter) {
    this.logFormatter = logFormatter;
  }

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
    throws IOException {

    String loggingId = UUID.randomUUID().toString();
    Logger logger = loggerFor(request);

    if (logger.isTraceEnabled()) {
      logger.trace(logFormatter.format(loggingId, request, body));
    }

    ClientHttpResponse response = execution.execute(request, body);

    if (logger.isTraceEnabled()) {
      response = new BufferingClientHttpResponseWrapper(response);
      logger.trace(logFormatter.format(loggingId, response));
    }

    return response;
  }

  private static Logger loggerFor(HttpRequest httpRequest) {
    return LoggerFactory.getLogger("ClientHttpRequest." + httpRequest.getURI().getHost());
  }

}
