package com.netcetera.girders.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

@Slf4j
class SpringLoggerFixTest {

  @Test
  void shouldRedefineLogger() throws Exception {
    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

    BufferingAppender bufferingAppender = new BufferingAppender();
    bufferingAppender.setName("test");
    bufferingAppender.setContext(context);
    bufferingAppender.start();

    context.getLogger(AbstractRequestLoggingFilter.class).setLevel(Level.TRACE);
    context.getLogger(AbstractRequestLoggingFilter.class).addAppender(bufferingAppender);

    ServletRequest request = new MockHttpServletRequest();
    ServletResponse response = new MockHttpServletResponse();
    FilterChain filterChain = new MockFilterChain();
    FilterConfig filterConfig = new MockFilterConfig("TestRequestLoggingFilter");

    Filter filter = new TestRequestLoggingFilter();

    filter.init(filterConfig);
    filter.doFilter(request, response, filterChain);

    assertThat(bufferingAppender.dumpCsv(), containsString("Filter 'TestRequestLoggingFilter'"));

    bufferingAppender.clear();
    bufferingAppender.stop();
  }

  @SuppressWarnings("ThisEscapedInObjectConstruction")
  @Slf4j
  static class TestRequestLoggingFilter extends AbstractRequestLoggingFilter {

    TestRequestLoggingFilter() {
      SpringLoggerFix.redefineSuperclassLogger(this, logger, AbstractRequestLoggingFilter.class);
    }


    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
      logger.info("Before request: {}", message);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
      logger.info("After request: {}", message);
    }
  }

}
