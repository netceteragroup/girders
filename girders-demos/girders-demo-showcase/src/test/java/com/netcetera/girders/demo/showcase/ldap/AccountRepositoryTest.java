package com.netcetera.girders.demo.showcase.ldap;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.LdapTemplate;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for the {@link AccountRepository} class.
 */
class AccountRepositoryTest {

  @SuppressWarnings("unchecked")
  @Test
  void shouldFindAll() {
    List<Account> mockAccounts = Lists.newArrayList();
    for (int i = 0; i < 4; ++i) {
      mockAccounts.add(new Account("id-" + i, "testFirstName", "testLastName", "testEmail"));
    }
    LdapTemplate ldapTemplate = mock(LdapTemplate.class);
    when(ldapTemplate.search(anyString(), anyString(), any(ContextMapper.class))).thenReturn(mockAccounts);
    AccountAttributesMapper accountAttributesMapper = new AccountAttributesMapper();
    AccountRepository repository = new AccountRepository(ldapTemplate, accountAttributesMapper);

    List<Account> accounts = repository.findAll();

    assertThat(accounts, is(not(nullValue())));
    assertThat(accounts.size(), is(4));
  }

}