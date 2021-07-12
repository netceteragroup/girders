package com.netcetera.girders.demo.showcase.home;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class HomeControllerTest {

  @Test
  void shouldHandleHomePageRequest() {
    HomeController controller = new HomeController();

    assertThat(controller.initHomePage(), is("home"));
  }

}