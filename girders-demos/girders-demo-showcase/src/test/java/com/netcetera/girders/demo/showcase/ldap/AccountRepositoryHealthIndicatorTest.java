package com.netcetera.girders.demo.showcase.ldap;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.health.Status;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for the {@link AccountRepositoryHealthIndicator} class.
 */
class AccountRepositoryHealthIndicatorTest {

  @Test
  void shouldReportUp() {
    AccountRepository repository = mock(AccountRepository.class);
    when(repository.findAll()).thenReturn(Lists.newArrayList(new Account()));

    AccountRepositoryHealthIndicator healthIndicator = new AccountRepositoryHealthIndicator(repository);
    Builder builder = new Builder(Status.UNKNOWN);

    healthIndicator.doHealthCheck(builder);
    Health health = builder.build();

    assertThat(health.getStatus(), is(Status.UP));
  }

  @Test
  void shouldReportDown() {
    AccountRepository repository = mock(AccountRepository.class);
    when(repository.findAll()).thenThrow(new RuntimeException());

    AccountRepositoryHealthIndicator healthIndicator = new AccountRepositoryHealthIndicator(repository);
    Builder builder = new Builder(Status.UNKNOWN);

    healthIndicator.doHealthCheck(builder);
    Health health = builder.build();

    assertThat(health.getStatus(), is(Status.DOWN));
  }

}