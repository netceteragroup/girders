package com.netcetera.girders.resttemplatelogging;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.springframework.util.StreamUtils.copyToByteArray;

/**
 * Wrapper for a {@link ClientHttpResponse} that buffers the response body.
 */
final class BufferingClientHttpResponseWrapper implements ClientHttpResponse {

  private final ClientHttpResponse response;

  private byte[] body;

  /**
   * @param response the response to wrap
   */
  BufferingClientHttpResponseWrapper(ClientHttpResponse response) {
    this.response = response;
  }


  @Override
  public HttpStatus getStatusCode() throws IOException {
    return response.getStatusCode();
  }

  @Override
  public int getRawStatusCode() throws IOException {
    return response.getRawStatusCode();
  }

  @Override
  public String getStatusText() throws IOException {
    return response.getStatusText();
  }

  @Override
  public void close() {
    response.close();
  }

  @Override
  public HttpHeaders getHeaders() {
    return response.getHeaders();
  }

  @Override
  public InputStream getBody() throws IOException {
    if (body == null) {
      body = copyToByteArray(response.getBody());
    }
    return new ByteArrayInputStream(body);
  }

}
