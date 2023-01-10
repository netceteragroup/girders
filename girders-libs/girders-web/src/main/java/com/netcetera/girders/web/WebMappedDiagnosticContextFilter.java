package com.netcetera.girders.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.MDC;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import java.security.Principal;
import java.util.UUID;

/**
 * Servlet filter that adds mapped diagnostic context (MDC) to the logging output.
 * <p>
 * The names for the MDC fields are based on
 * <a href="https://www.elastic.co/guide/en/ecs/current/">Elastic Common Schema (ECS)</a>.
 */
public class WebMappedDiagnosticContextFilter extends AbstractRequestLoggingFilter {

  /**
   * User identifier field.
   */
  protected static final String USER = "user.id";

  /**
   * Session identifier field.
   */
  protected static final String SESSION = "session.id";

  /**
   * Request or transaction identifier field.
   */
  protected static final String TRANSACTION_ID = "transaction.id";

  /**
   * Remote IP address field.
   */
  protected static final String CLIENT_IP = "client.ip";

  @Override
  protected void beforeRequest(HttpServletRequest request, String message) {
    Principal principal = request.getUserPrincipal();
    HttpSession session = request.getSession(false);

    String username = (principal != null) ? principal.getName() : null;
    String sessionId = (session != null) ? session.getId() : null;

    if (username != null) {
      MDC.put(USER, username);
    }

    if (sessionId != null) {
      MDC.put(SESSION, sessionId);
    }

    MDC.put(TRANSACTION_ID, UUID.randomUUID().toString());

    MDC.put(CLIENT_IP, ClientIpAddress.getFrom(request, false));
  }

  @Override
  protected void afterRequest(HttpServletRequest request, String message) {
    MDC.remove(USER);
    MDC.remove(SESSION);
    MDC.remove(TRANSACTION_ID);
    MDC.remove(CLIENT_IP);
  }

}
