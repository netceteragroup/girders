package com.netcetera.girders.demo.test.actuator;

import com.netcetera.girders.demo.test.TestConfiguration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Integration test for the {@code info} actuator resource.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,
    classes = com.netcetera.girders.demo.showcase.GirdersDemoShowcaseApplication.class,
    properties = "girders.crypto.text=disabled")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfiguration.class)
@Slf4j
class IndexTest {

  @Autowired
  @Qualifier("testClientHttpRequestFactory")
  private ClientHttpRequestFactory client;

  @LocalServerPort
  private int port;

  @Test
  @Disabled
  @SneakyThrows
  void testGet() {
    String url = "http://localhost:" + port + "/demo-showcase/";

    ClientHttpResponse response = client.createRequest(URI.create(url), HttpMethod.GET).execute();
    String content = IOUtils.toString(response.getBody(), StandardCharsets.UTF_8);

    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(content, containsString("Girders Showcase Application"));
    assertThat(content, not(containsString("Error")));
  }

}
