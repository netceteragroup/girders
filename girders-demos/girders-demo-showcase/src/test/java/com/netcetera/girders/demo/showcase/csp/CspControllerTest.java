package com.netcetera.girders.demo.showcase.csp;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class CspControllerTest {

  @Test
  void shouldDisplayPage() {
    assertThat(new CspController().displayPage(), is("csp"));
  }

}