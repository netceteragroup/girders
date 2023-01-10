package com.netcetera.girders.demo.showcase.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Handler interceptor that adds version information to the request.
 */
@Component
class AppInfoInterceptor implements HandlerInterceptor {

  private static final String ATTRIBUTE_APP_VERSION = "appVersion";

  private static final String ATTRIBUTE_GIRDERS_VERSION = "girdersVersion";

  private static final String ATTRIBUTE_SPRINGBOOT_VERSION = "springBootVersion";

  @Value("${info.app.name:?}")
  @Setter
  private String appName;

  @Value("${info.app.version:?}")
  @Setter
  private String appVersion;

  @Value("${info.app.girders.version:?}")
  @Setter
  private String girdersVersion;

  @Value("${info.app.spring-boot.version:?}")
  @Setter
  private String springBootVersion;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

    request.setAttribute(ATTRIBUTE_APP_VERSION, appName + ' ' + appVersion);
    request.setAttribute(ATTRIBUTE_GIRDERS_VERSION, "Girders " + girdersVersion);
    request.setAttribute(ATTRIBUTE_SPRINGBOOT_VERSION, "Spring Boot " + springBootVersion);

    return true;
  }

}
