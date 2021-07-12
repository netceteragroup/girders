package com.netcetera.girders.resttemplatelogging;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.lang.String.join;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.util.StreamUtils.copyToByteArray;

/**
 * {@link LogFormatter} which pretty prints HTTP request/responses in a textual,
 * human readable way over multiple lines.
 */
@SuppressWarnings("HardcodedLineSeparator")
public class PrettyPrintLogFormatter implements LogFormatter {

  @Override
  public String format(String loggingId, HttpRequest request, byte[] body) {
    StringBuilder sb = new StringBuilder();

    sb.append("\n8<-------------------- vvv\n");

    sb.append("URI: ").append(request.getURI()).append('\n');
    sb.append("Method: ").append(request.getMethod()).append('\n');
    sb.append('\n');

    appendHeaders(sb, request.getHeaders());

    appendBody(sb, body);

    sb.append("\n8<-------------------- ^^^\n");

    return String.format("ID: %s / Request: %s", loggingId, sb.toString());
  }

  @Override
  public String format(String loggingId, ClientHttpResponse response) throws IOException {
    StringBuilder sb = new StringBuilder();

    sb.append("\n8<-------------------- vvv\n");

    sb.append("StatusCode: ").append(response.getStatusCode()).append('\n');
    sb.append('\n');

    appendHeaders(sb, response.getHeaders());

    appendBody(sb, copyToByteArray(response.getBody()));

    sb.append("\n8<-------------------- ^^^\n");

    return String.format("ID: %s / Response: %s", loggingId, sb.toString());
  }

  private static void appendHeaders(StringBuilder sb, Map<String, List<String>> headers) {
    if (!headers.isEmpty()) {
      sb.append("Headers\n");
      sb.append("-------\n");
      for (Entry<String, List<String>> header : headers.entrySet()) {
        sb.append(header.getKey()).append(": ").append(join(", ", header.getValue())).append('\n');
      }
    }
    sb.append('\n');
  }

  private static void appendBody(StringBuilder sb, byte[] body) {
    if (body.length > 0) {
      sb.append("Body\n");
      sb.append("----\n");
      /*
       * One would have to look into the content to determine the correct charset. The
       * strategies for doing so obviously depend on the content type. Hence, it'd be
       * a lot of work. Assuming UTF-8 here is about as (un)safe as using platform
       * encoding i.e. not defining one but at least it's defined and consistent.
       */
      sb.append(new String(body, UTF_8));
    }
  }

}
