package com.netcetera.girders.demo.showcase.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * {HandlerInterceptor} that measures the time for the processing of the request
 * and puts the result in the request context.
 */
@Slf4j
@Component
public class TimerInterceptor extends HandlerInterceptorAdapter {

  private static final String ATTRIBUTE_START = "timerStart";
  private static final String ATTRIBUTE_STOP = "timerStop";
  private static final String ATTRIBUTE_DURATION = "timerDuration";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

    // set the start time
    request.setAttribute(ATTRIBUTE_START, System.currentTimeMillis());

    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
      ModelAndView modelAndView) {

    // set the end time
    long endTime = System.currentTimeMillis();
    request.setAttribute(ATTRIBUTE_STOP, endTime);

    // get the start time
    long startTime = (Long) request.getAttribute(ATTRIBUTE_START);

    // set the duration
    long duration = endTime - startTime;
    logger.debug("Duration: {}ms.", duration);

    // Note: In a Spring Web Flow setting, the view will already be rendered at this
    // point.
    // See also http://stackoverflow.com/questions/9687084
    request.setAttribute(ATTRIBUTE_DURATION, duration);
  }

}
