package com.netcetera.girders.demo.showcase.ldap;

import org.junit.jupiter.api.Test;
import org.springframework.ldap.core.DirContextAdapter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for the {@link AccountAttributesMapper} class.
 */
class AccountAttributesMapperTest {

  @Test
  void shouldMapFromContext() {
    DirContextAdapter context = mock(DirContextAdapter.class);
    when(context.getStringAttribute(eq("uid"))).thenReturn("42");
    when(context.getStringAttribute(eq("cn"))).thenReturn("testFirstName");
    when(context.getStringAttribute(eq("sn"))).thenReturn("testLastName");
    when(context.getStringAttribute(eq("mail"))).thenReturn("testEmail");

    AccountAttributesMapper mapper = new AccountAttributesMapper();

    Account account = mapper.mapFromContext(context);

    assertThat(account.getUid(), is("42"));
    assertThat(account.getFirstName(), is("testFirstName"));
    assertThat(account.getLastName(), is("testLastName"));
    assertThat(account.getEmail(), is("testEmail"));
  }

}