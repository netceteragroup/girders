package com.netcetera.girders.demo.showcase.interceptor;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class AppInfoInterceptorTest {

  @Test
  void shouldPreHandle() {
    MockHttpServletRequest request = new MockHttpServletRequest();

    AppInfoInterceptor interceptor = new AppInfoInterceptor();
    interceptor.setAppName("testAppName");
    interceptor.setAppVersion("testAppVersion");
    interceptor.setGirdersVersion("testGirdersVersion");
    interceptor.setSpringBootVersion("testSpringBootVersion");

    assertThat(interceptor.preHandle(request, null, null), is(true));
    assertThat(request.getAttribute("appVersion"), is("testAppName testAppVersion"));
    assertThat(request.getAttribute("girdersVersion"), is("Girders testGirdersVersion"));
    assertThat(request.getAttribute("springBootVersion"), is("Spring Boot testSpringBootVersion"));
  }

}