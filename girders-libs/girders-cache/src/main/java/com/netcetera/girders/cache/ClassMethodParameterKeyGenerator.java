package com.netcetera.girders.cache;


import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.interceptor.SimpleKeyGenerator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Class method parameter considering key generator.
 */
@Slf4j
public class ClassMethodParameterKeyGenerator extends SimpleKeyGenerator {

  @Override
  public Object generate(Object target, Method method, Object... params) {
    Collection<Object> key = new ArrayList<>(2 + params.length);
    key.add(target.getClass().getName());
    key.add(method.getName());
    Collections.addAll(key, super.generate(target, method, params));
    logger.trace("Custom cache key generated: '{}'", key);
    return key;
  }

}
