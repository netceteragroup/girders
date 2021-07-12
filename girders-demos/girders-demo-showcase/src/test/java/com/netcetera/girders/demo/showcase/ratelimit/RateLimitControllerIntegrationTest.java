package com.netcetera.girders.demo.showcase.ratelimit;

import com.netcetera.girders.ratelimit.support.RateLimitExceededException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.ExtendedModelMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = "jasypt.encryptor.password=girders")
class RateLimitControllerIntegrationTest {

  @Autowired
  private RateLimitController controller;

  @Test
  void shouldDisplayPage() {
    assertThat(controller.displayPage(), is("rate-limit"));
  }

  @Test
  void shouldExecuteActionWithGlobalRateLimit() {
    assertThat(controller.executeActionWithGlobalRateLimit(), is("redirect:/rate-limit"));
    assertThat(controller.executeActionWithGlobalRateLimit(), is("redirect:/rate-limit"));
    assertThat(controller.executeActionWithGlobalRateLimit(), is("redirect:/rate-limit"));

    RateLimitExceededException e = assertThrows(RateLimitExceededException.class, () -> {
      controller.executeActionWithGlobalRateLimit();
    });
    assertThat(controller.handleRateLimitExceededException(e), is("rate-limit-exceeded"));
  }

  @Test
  void shouldExecuteActionWithKeyedRateLimit() {
    assertThat(controller.executeActionWithKeyedRateLimit("test", new ExtendedModelMap()), is("redirect:/rate-limit"));
    RateLimitExceededException e = assertThrows(RateLimitExceededException.class, () -> {
      controller.executeActionWithKeyedRateLimit("test", new ExtendedModelMap());
    });
    assertThat(controller.handleRateLimitExceededException(e), is("rate-limit-exceeded"));
  }

}