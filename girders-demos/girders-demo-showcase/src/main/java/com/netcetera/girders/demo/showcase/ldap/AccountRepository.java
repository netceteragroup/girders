package com.netcetera.girders.demo.showcase.ldap;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.jperf.aop.Profiled;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;

/**
 * {@link Repository} for {@link Account}s based on an LDAP directory.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
class AccountRepository {

  private final LdapTemplate ldapTemplate;

  private final AccountAttributesMapper accountAttributesMapper;

  /**
   * Find all the accounts.
   *
   * @return List of accounts
   */
  @Profiled(tag = "LdapRepository.findAll")
  @Timed
  @Cacheable("accounts")
  public List<Account> findAll() {
    Filter accountFilter = new EqualsFilter("employeeType", "ncg-employee");
    List<Account> accounts = ldapTemplate.search("ou=people", accountFilter.encode(), accountAttributesMapper);
    accounts.sort(Comparator.comparing(Account::getUid));
    return accounts;
  }

}
