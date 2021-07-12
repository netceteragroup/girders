package com.netcetera.girders.demo.showcase;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.BeanNameViewResolver;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class WebConfigurationTest {

  @Test
  void shouldProvideCommonsRequestLoggingFilter() {
    WebConfiguration config = new WebConfiguration(Lists.newArrayList());

    assertThat(config.commonsRequestLoggingFilter(), is(not(nullValue())));
  }

  @Test
  void shouldProvideBeanNameViewResolver() {
    WebConfiguration config = new WebConfiguration(Lists.newArrayList());

    ViewResolverRegistry registry = mock(ViewResolverRegistry.class);
    config.configureViewResolvers(registry);

    ArgumentCaptor<ViewResolver> argumentCaptor = ArgumentCaptor.forClass(ViewResolver.class);
    verify(registry).viewResolver(argumentCaptor.capture());

    ViewResolver viewResolver = argumentCaptor.getValue();
    assertThat(viewResolver, instanceOf(BeanNameViewResolver.class));
    BeanNameViewResolver beanNameViewResolver = (BeanNameViewResolver) viewResolver;
    assertThat(beanNameViewResolver.getOrder(), is(0));
  }

  @Test
  void shouldSupportAddingInterceptors() {
    List<HandlerInterceptor> handlerInterceptors = Lists.newArrayList(mock(HandlerInterceptor.class),
        mock(HandlerInterceptor.class), mock(HandlerInterceptor.class));
    InterceptorRegistry interceptorRegistry = mock(InterceptorRegistry.class);

    WebMvcConfigurer config = new WebConfiguration(handlerInterceptors);

    config.addInterceptors(interceptorRegistry);

    verify(interceptorRegistry, times(3)).addInterceptor(any(HandlerInterceptor.class));
  }


}
