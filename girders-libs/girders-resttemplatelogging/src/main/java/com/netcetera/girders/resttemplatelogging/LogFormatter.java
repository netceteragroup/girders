package com.netcetera.girders.resttemplatelogging;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Abstraction for a log formatter of HTTP requests/responses.
 */
public interface LogFormatter {

  /**
   * Logs the given HTTP request.
   *
   * @param loggingId An ID used to correlate the logging of an HTTP request with
   * its corresponding HTTP response.
   * @param request The HTTP request to log.
   * @param body The body of the HTTP request to log.
   * @return The string to log for the HTTP request.
   * @throws IOException in case of an error
   */
  String format(String loggingId, HttpRequest request, byte[] body) throws IOException;

  /**
   * Logs the given HTTP response.
   *
   * @param loggingId An ID used to correlate the logging of an HTTP request with
   * its corresponding HTTP response.
   * @param response The HTTP response to log.
   * @return The string to log for the HTTP response.
   * @throws IOException in case of an error
   */
  String format(String loggingId, ClientHttpResponse response) throws IOException;

}
