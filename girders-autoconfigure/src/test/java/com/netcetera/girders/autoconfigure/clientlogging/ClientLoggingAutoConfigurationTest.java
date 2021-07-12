package com.netcetera.girders.autoconfigure.clientlogging;

import com.netcetera.girders.clientlogging.ClientLoggingController;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Unit test for the {@link ClientLoggingAutoConfiguration} class.
 */
class ClientLoggingAutoConfigurationTest {

  @Test
  void shouldReturnClientLoggingController() {

    // given
    ClientLoggingAutoConfiguration config = new ClientLoggingAutoConfiguration();

    // when
    ClientLoggingController controller = config.clientLoggingController();

    // then
    assertThat(controller, is(notNullValue()));
  }

}