package com.netcetera.girders.autoconfigure.ratelimit;

import com.netcetera.girders.ratelimit.ArgRateLimitKeyResolver;
import com.netcetera.girders.ratelimit.RateLimit;
import com.netcetera.girders.ratelimit.RateLimitKeyResolver;
import com.netcetera.girders.ratelimit.RequestRateLimitKeyResolver;
import com.netcetera.girders.ratelimit.support.RateLimitSpecParser;
import com.netcetera.girders.ratelimit.support.RateLimitValuesRetriever;
import com.netcetera.girders.ratelimit.support.RateLimitingAspect;
import com.netcetera.girders.web.ClientIpAddress;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringValueResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * Auto configuration for annotation based rate limit support.
 */
@Configuration
@ConditionalOnClass(RateLimit.class)
@EnableConfigurationProperties(RateLimitProperties.class)
public class RateLimitAutoConfiguration implements EmbeddedValueResolverAware {

  private StringValueResolver embeddedValueResolver;
  private final MeterRegistry meterRegistry;

  /**
   * Constructor.
   *
   * @param meterRegistry Meter registry (optional)
   */
  public RateLimitAutoConfiguration(@Autowired(required = false) MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  @Override
  public void setEmbeddedValueResolver(StringValueResolver embeddedValueResolver) {
    this.embeddedValueResolver = embeddedValueResolver;
  }

  /**
   * Bean for an argument based {@link RateLimitKeyResolver}.
   *
   * @return the argument key resolver
   */
  @Bean
  public ArgRateLimitKeyResolver argRateLimitKeyResolver() {
    return new ArgRateLimitKeyResolver();
  }

  /**
   * Aspect bean which intercepts the call of rate limited methods and applies the
   * rate limiting policy.
   *
   * @param rateLimitKeyResolvers the list of rate limit key resolvers
   *
   * @return the rate limiting aspect
   */
  @Bean
  public RateLimitingAspect rateLimitingAspect(
      List<RateLimitKeyResolver> rateLimitKeyResolvers,
      RateLimitProperties rateLimitProperties) {
    RateLimitSpecParser rateLimitSpecParser = new RateLimitSpecParser();
    RateLimitValuesRetriever rateLimitValuesRetriever = new RateLimitValuesRetriever(rateLimitSpecParser,
        embeddedValueResolver);

    return new RateLimitingAspect(rateLimitValuesRetriever, rateLimitKeyResolvers, meterRegistry,
        rateLimitProperties.isEnableMeterRegistry());
  }

  /**
   * Create an instance of a {@link RequestRateLimitKeyResolver}.
   *
   * @return Bean
   */
  @Bean
  @ConditionalOnClass({HttpServletRequest.class, ClientIpAddress.class})
  public RequestRateLimitKeyResolver requestRateLimitKeyResolver() {
    return new RequestRateLimitKeyResolver();
  }

}
