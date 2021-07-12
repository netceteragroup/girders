package com.netcetera.girders.demo.showcase.ldap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.stereotype.Component;

/**
 * {@code HealthIndicator} for the {@link AccountRepository} class.
 */
@Component
@RequiredArgsConstructor
class AccountRepositoryHealthIndicator extends AbstractHealthIndicator {

  private final AccountRepository repository;

  @SuppressWarnings("OverlyBroadCatchBlock")
  @Override
  protected void doHealthCheck(Builder builder) {
    try {
      int nofAccounts = repository.findAll().size();
      builder.up().withDetail("message", nofAccounts + " accounts are in the repository");
    } catch (Exception e) {
      builder.down(e);
    }
  }

}
