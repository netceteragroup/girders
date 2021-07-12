package com.netcetera.girders.resttemplatelogging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.util.StreamUtils.copyToByteArray;

/**
 * {@link LogFormatter} which prints HTTP request/responses as a JSON object on
 * a single line.
 */
public class JsonLogFormatter implements LogFormatter {

  private static final String CORRELATION_FIELD = "correlation";
  private static final String TYPE_FIELD = "type";
  private static final String URI_FIELD = "uri";
  private static final String METHOD_FIELD = "method";
  private static final String STATUS_FIELD = "status";
  private static final String HEADERS_FIELD = "headers";
  private static final String BODY_FIELD = "body";

  // a local ObjectMapper is used to avoid getting undesired configuration
  // from a globally configured ObjectMapper Spring bean
  private final ObjectMapper jsonMapper = new ObjectMapper();

  @Override
  public String format(String loggingId, HttpRequest request, byte[] body) throws IOException {
    ObjectNode requestJson = jsonMapper.createObjectNode();

    requestJson.put(CORRELATION_FIELD, loggingId);
    requestJson.put(TYPE_FIELD, "request");
    requestJson.put(URI_FIELD, request.getURI().toString());
    requestJson.put(METHOD_FIELD, String.valueOf(request.getMethod()));

    if (!request.getHeaders().isEmpty()) {
      requestJson.set(HEADERS_FIELD, getHeadersJson(request.getHeaders()));
    }

    if (body.length > 0) {
      requestJson.put(BODY_FIELD, getBodyAsString(body));
    }

    return jsonMapper.writeValueAsString(requestJson);
  }

  @Override
  public String format(String loggingId, ClientHttpResponse response) throws IOException {
    byte[] body = copyToByteArray(response.getBody());

    ObjectNode responseJson = jsonMapper.createObjectNode();

    responseJson.put(CORRELATION_FIELD, loggingId);
    responseJson.put(TYPE_FIELD, "response");
    responseJson.put(STATUS_FIELD, response.getStatusCode().value());

    if (!response.getHeaders().isEmpty()) {
      responseJson.set(HEADERS_FIELD, getHeadersJson(response.getHeaders()));
    }

    if (body.length > 0) {
      responseJson.put(BODY_FIELD, getBodyAsString(body));
    }

    return jsonMapper.writeValueAsString(responseJson);
  }

  private static String getBodyAsString(byte[] body) {
    /*
     * One would have to look into the content to determine the correct charset. The
     * strategies for doing so obviously depend on the content type. Hence, it'd be
     * a lot of work. Assuming UTF-8 here is about as (un)safe as using platform
     * encoding i.e. not defining one but at least it's defined and consistent.
     */
    return new String(body, UTF_8);
  }

  private ObjectNode getHeadersJson(Map<String, List<String>> headers) {
    ObjectNode headersJson = jsonMapper.createObjectNode();
    headers.forEach((headerName, headerValues) -> {
      ArrayNode headerValuesJson = jsonMapper.createArrayNode();
      headerValues.forEach(headerValuesJson::add);
      headersJson.set(headerName, headerValuesJson);
    });
    return headersJson;
  }

}
