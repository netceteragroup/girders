package com.netcetera.girders.demo.showcase.ratelimit;

import com.netcetera.girders.ratelimit.RateLimit;
import com.netcetera.girders.ratelimit.support.RateLimitExceededException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.netcetera.girders.ratelimit.ArgRateLimitKeyResolver.ARG_0;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * {@link Controller} for the {@code rate-limit} resource.
 */
@Controller
@RequestMapping("/rate-limit")
public class RateLimitController {

  private static final String VIEW_NAME = "rate-limit";

  private static final String VIEW_NAME_RATE_LIMIT_EXCEEDED = "rate-limit-exceeded";

  /**
   * Process a {@link org.springframework.web.bind.annotation.RequestMethod#GET} request.
   *
   * @return View name
   */
  @RequestMapping(method = GET)
  public String displayPage() {
    return VIEW_NAME;
  }

  /**
   * Process a {@link org.springframework.web.bind.annotation.RequestMethod#POST} request for the {@code global}
   * resource.
   *
   * @return View name
   */
  @SuppressWarnings("SameReturnValue")
  @RequestMapping(path = "/global", method = POST)
  @RateLimit(spec = "${showcase.rate-limit.global-rate-limit-spec}")
  public String executeActionWithGlobalRateLimit() {
    return "redirect:/rate-limit";
  }

  /**
   * Process a {@link org.springframework.web.bind.annotation.RequestMethod#POST} request for the {@code keyed}
   * resource.
   *
   * @param rateLimitKey Key for the rate limiting
   * @param model        Model for the request
   *
   * @return View name
   */
  @SuppressWarnings("SameReturnValue")
  @RequestMapping(path = "/keyed", method = POST)
  @RateLimit(key = ARG_0, limit = 1, per = MINUTES)
  public String executeActionWithKeyedRateLimit(String rateLimitKey, Model model) {
    model.addAttribute("rateLimitKey", rateLimitKey);
    return "redirect:/rate-limit";
  }

  /**
   * Error handler in case the rate limit is exceeded.
   *
   * @param ex Exception that signals the exceeding of the rate limit
   *
   * @return View name
   */
  @ExceptionHandler(RateLimitExceededException.class)
  public String handleRateLimitExceededException(RateLimitExceededException ex) {
    return VIEW_NAME_RATE_LIMIT_EXCEEDED;
  }

}
