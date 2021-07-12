package com.netcetera.girders.demo.showcase.interceptor;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;

class TimerInterceptorTest {

  @Test
  void shouldWrapRequest() {
    MockHttpServletRequest request = new MockHttpServletRequest();

    TimerInterceptor interceptor = new TimerInterceptor();

    assertThat(interceptor.preHandle(request, null, null), is(true));
    interceptor.postHandle(request, null, null, null);

    assertThat(((Long) request.getAttribute("timerStart")), is(greaterThanOrEqualTo(0L)));
    assertThat(((Long) request.getAttribute("timerStop")), is(greaterThanOrEqualTo(0L)));
    assertThat(((Long) request.getAttribute("timerDuration")), is(greaterThanOrEqualTo(0L)));
  }

}