package com.netcetera.girders.demo.test.actuator;

import com.fasterxml.jackson.databind.JsonNode;
import com.netcetera.girders.demo.test.TestConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestOperations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for the {@code info} actuator resource.
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,
    classes = com.netcetera.girders.demo.showcase.GirdersDemoShowcaseApplication.class,
    properties = "girders.crypto.text=disabled")
@Import(TestConfiguration.class)
@ExtendWith(SpringExtension.class)
class InfoTest {

  @Autowired
  @Qualifier("testRestTemplate")
  private RestOperations client;

  @LocalServerPort
  private int port;

  @Test
  void testInfo() {
    String url = "http://localhost:" + port + "/demo-showcase/actuator/info";
    JsonNode result = client.getForObject(url, JsonNode.class);
    assertTrue(!StringUtils.contains(result.path("app").path("version").asText(), '?'));
    assertTrue(!StringUtils.contains(result.path("app").path("version").asText(), '{'));
    assertTrue(!StringUtils.contains(result.path("app").path("version").asText(), '}'));
  }

}
