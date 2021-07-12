package com.netcetera.girders.resttemplatelogging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.util.StreamUtils.copyToByteArray;

/**
 * Tests for the {@link BufferingClientHttpResponseWrapper}.
 */
class BufferingClientHttpResponseWrapperTest {

  private ClientHttpResponse response;
  private BufferingClientHttpResponseWrapper wrapper;

  @BeforeEach
  void setUp() {
    this.response = mock(ClientHttpResponse.class);
    this.wrapper = new BufferingClientHttpResponseWrapper(this.response);
  }

  @Test
  void getStatusCode() throws IOException {
    when(this.response.getStatusCode()).thenReturn(CREATED);
    assertThat(this.wrapper.getStatusCode(), is(CREATED));
  }

  @Test
  void getRawStatusCode() throws IOException {
    when(this.response.getRawStatusCode()).thenReturn(203);
    assertThat(this.wrapper.getRawStatusCode(), is(203));
  }

  @Test
  void getStatusText() throws IOException {
    when(this.response.getStatusText()).thenReturn("statusText");
    assertThat(this.wrapper.getStatusText(), is("statusText"));
  }

  @Test
  void close() {
    this.wrapper.close();
    verify(this.response).close();
  }

  @Test
  void getHeaders() {
    HttpHeaders headers = new HttpHeaders();
    when(this.response.getHeaders()).thenReturn(headers);
    assertThat(this.wrapper.getHeaders(), is(headers));
  }

  @Test
  void getBody() throws IOException {
    byte[] expected = "SomeString".getBytes(UTF_8);
    InputStream inputStream = new ByteArrayInputStream(expected);
    when(this.response.getBody()).thenReturn(inputStream);

    String actual = new String(copyToByteArray(this.wrapper.getBody()), UTF_8);
    assertThat(actual, is("SomeString"));
  }
}
