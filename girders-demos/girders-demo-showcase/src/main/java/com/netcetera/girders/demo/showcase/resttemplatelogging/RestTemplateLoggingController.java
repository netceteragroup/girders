package com.netcetera.girders.demo.showcase.resttemplatelogging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * {@link Controller} for the {@code rest-template-logging} resource.
 */
@Controller
@RequestMapping("/rest-template-logging")
public class RestTemplateLoggingController {

  private static final String VIEW_NAME = "rest-template-logging";

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;
  private final String wikipediaApiUrl;

  /**
   * Constructor.
   *
   * @param restTemplate    Template for REST calls
   * @param objectMapper    Mapper for input and output objects
   * @param wikipediaApiUrl URL for the Wikipedia resource
   */
  public RestTemplateLoggingController(
      @NonNull @Qualifier("loggingRestTemplate") RestTemplate restTemplate,
      @NonNull ObjectMapper objectMapper,
      @Value("${showcase.rest-template-logging.wikipedia-api-url}") String wikipediaApiUrl) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
    this.wikipediaApiUrl = wikipediaApiUrl;
  }

  /**
   * Process a {@link org.springframework.web.bind.annotation.RequestMethod#GET} request.
   *
   * @param model Model for the request
   *
   * @return View name
   */
  @SuppressWarnings("rawtypes")
  @RequestMapping(method = GET)
  public String displayPage(@NonNull Model model) {
    ObjectNode wikipediaArticlesResponse = restTemplate.getForObject(wikipediaApiUrl, ObjectNode.class);

    if (wikipediaArticlesResponse != null) {
      List articles = objectMapper.convertValue(wikipediaArticlesResponse.path("query").path("random"),
          List.class);
      model.addAttribute("articles", articles);
    }

    return VIEW_NAME;
  }

}
