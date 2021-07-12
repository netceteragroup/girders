package com.netcetera.girders.demo.showcase.auth;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class LoginControllerTest {

  @Test
  void shouldLogin() {
    assertThat(new LoginController().login(), is("login"));
  }

}