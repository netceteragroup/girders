package com.netcetera.girders.autoconfigure.ratelimit;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ToString
@Getter
@Setter
@ConfigurationProperties(prefix = "girders.rate-limit", ignoreUnknownFields = false)
public class RateLimitProperties {

  private boolean enableMeterRegistry = true;
}
