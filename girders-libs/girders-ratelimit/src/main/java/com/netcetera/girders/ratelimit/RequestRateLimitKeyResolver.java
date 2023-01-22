package com.netcetera.girders.ratelimit;

import com.netcetera.girders.web.ClientIpAddress;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;

/**
 * Rate limit key resolver for HTTP servlet request data.
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Slf4j
public class RequestRateLimitKeyResolver implements RateLimitKeyResolver {

  /**
   * Request IP (i.e. client IP) key.
   */
  public static final String IP = "@req.ip";
  /**
   * Username key.
   */
  public static final String USERNAME = "@req.username";

  private static final List<String> KEYS = newArrayList(IP, USERNAME);

  @Autowired
  private HttpServletRequest request;

  @Override
  public boolean canResolve(RateLimit rateLimit) {
    logger.info("canResolve({})", rateLimit);
    return KEYS.contains(rateLimit.key());
  }

  @Override
  public RateLimitKey resolve(JoinPoint joinPoint, RateLimit rateLimit) {
    switch (rateLimit.key()) {
      case IP:
        return new RateLimitKey(ClientIpAddress.getFrom(request, false));
      case USERNAME:
        return new RateLimitKey(request.getRemoteUser());
      default:
        throw new IllegalArgumentException(format("Unsupported key: %s", rateLimit.key()));
    }
  }
}
