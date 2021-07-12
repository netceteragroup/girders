package com.netcetera.girders.demo.showcase;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.BeanNameViewResolver;

import java.util.Collection;

/**
 * {@link Configuration} for the web layer.
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

  private final Collection<HandlerInterceptor> handlerInterceptors;

  /**
   * Constructor.
   *
   * @param handlerInterceptors Handler interceptors
   */
  @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
  public WebConfiguration(Collection<HandlerInterceptor> handlerInterceptors) {
    this.handlerInterceptors = handlerInterceptors;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    handlerInterceptors.forEach(registry::addInterceptor);
  }

  /**
   * Create an instance of the request logging filter.
   *
   * @return Filter
   */
  @Bean
  public CommonsRequestLoggingFilter commonsRequestLoggingFilter() {
    return new CommonsRequestLoggingFilter();
  }

  @Override
  public void configureViewResolvers(ViewResolverRegistry registry) {
    BeanNameViewResolver viewResolver = new BeanNameViewResolver();
    viewResolver.setOrder(0);
    registry.viewResolver(viewResolver);
  }
}
