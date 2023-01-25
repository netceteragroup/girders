package com.netcetera.girders.demo.test.actuator;

import com.fasterxml.jackson.databind.JsonNode;
import com.netcetera.girders.demo.test.TestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration test for the {@code health} actuator resource.
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,
    classes = com.netcetera.girders.demo.showcase.GirdersDemoShowcaseApplication.class,
    properties = { "girders.crypto.text=disabled", "management.health.ldap.enabled=false" })
@Import(TestConfiguration.class)
@ExtendWith(SpringExtension.class)
@Slf4j
class HealthTest {

  @Autowired
  @Qualifier("testRestTemplate")
  private RestTemplate client;

  @LocalServerPort
  private int port;

  @Test
  void testHealth() {
    String url = "http://localhost:" + port + "/demo-showcase/actuator/health";
    JsonNode result = client.getForObject(url, JsonNode.class);
    // TODO: Remove this tmp line
    logger.info("Actuator health result:\n {}", result.toPrettyString());
    assertEquals("UP", result.path("status").asText());
  }

}
