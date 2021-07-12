package com.netcetera.girders.demo.test;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.MimeType;

import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

/**
 * Integration test for the POI features.
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,
    classes = com.netcetera.girders.demo.showcase.GirdersDemoShowcaseApplication.class,
    properties = "girders.crypto.text=disabled")
@Import(TestConfiguration.class)
@ExtendWith(SpringExtension.class)
@Slf4j
class PdfTest {

  @Autowired
  @Qualifier("testClientHttpRequestFactory")
  private ClientHttpRequestFactory requestFactory;

  @LocalServerPort
  private int port;

  @Test
  @SneakyThrows
  void testPdfbox() {
    String url = "http://localhost:" + port + "/demo-showcase/pdf/pdfbox";
    ClientHttpResponse response = requestFactory.createRequest(new URI(url), HttpMethod.GET).execute();

    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(IOUtils.toByteArray(response.getBody()).length, is(greaterThan(1000)));
    assertThat(response.getHeaders().getContentType().isCompatibleWith(MimeType.valueOf("application/pdf")), is(true));
  }

  @Test
  @SneakyThrows
  void testFop() {
    String url = "http://localhost:" + port + "/demo-showcase/pdf/fop";
    ClientHttpResponse response = requestFactory.createRequest(new URI(url), HttpMethod.GET).execute();

    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(IOUtils.toByteArray(response.getBody()).length, is(greaterThan(1000)));
    assertThat(response.getHeaders().getContentType().isCompatibleWith(MimeType.valueOf("application/pdf")), is(true));
  }

}
