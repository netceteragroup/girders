package com.netcetera.girders.ratelimit;

import org.aspectj.lang.JoinPoint;

import java.util.regex.Pattern;

/**
 * Rate limit key based on a method argument.
 */
public class ArgRateLimitKeyResolver implements RateLimitKeyResolver {

  /**
   * Argument with index 0.
   */
  public static final String ARG_0 = "@args[0]";

  /**
   * Argument with index 1.
   */
  public static final String ARG_1 = "@args[1]";

  /**
   * Argument with index 2.
   */
  public static final String ARG_2 = "@args[2]";

  private static final Pattern ARGS_PATTERN = Pattern.compile("@args\\[\\d*]");

  @Override
  public boolean canResolve(RateLimit rateLimit) {
    return ARGS_PATTERN.matcher(rateLimit.key()).matches();
  }

  @Override
  public RateLimitKey resolve(JoinPoint joinPoint, RateLimit rateLimit) {
    int index = Integer.parseInt(rateLimit.key().replace("@args[", "").replace("]", ""));
    return new RateLimitKey(String.valueOf(joinPoint.getArgs()[index]));
  }
}
