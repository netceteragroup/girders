package com.netcetera.girders.starter.cache;

import org.springframework.cache.annotation.Cacheable;

/**
 * Service with @Cacheable annotations.
 */
public class CacheTestService {

  private int i = 1;

  @Cacheable("testCache")
  public int incrementAndReturnValue() {
    return i++;
  }
}
