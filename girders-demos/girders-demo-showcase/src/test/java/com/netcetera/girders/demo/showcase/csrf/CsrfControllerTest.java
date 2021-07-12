package com.netcetera.girders.demo.showcase.csrf;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class CsrfControllerTest {

  @Test
  void shouldDisplayPage() {
    assertThat(new CsrfController().displayPage(), is("csrf"));
  }

  @Test
  void shouldExecuteTargetAction() {
    assertThat(new CsrfController().executeTargetAction(), is("csrf"));
  }

}