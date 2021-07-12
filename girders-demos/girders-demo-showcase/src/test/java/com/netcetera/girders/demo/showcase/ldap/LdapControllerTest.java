package com.netcetera.girders.demo.showcase.ldap;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for the {@link LdapController} class.
 */
class LdapControllerTest {

  @SuppressWarnings("unchecked")
  @Test
  void shouldGetResults() {
    List<Account> accounts = Lists.newArrayList(new Account("testUid", "testFirstName", "testLastName", "testEmail"));
    AccountRepository repository = mock(AccountRepository.class);
    when(repository.findAll()).thenReturn(accounts);
    Model model = new ConcurrentModel();
    LdapController controller = new LdapController(repository);

    String view = controller.get(model);

    assertThat(view, is("ldap"));
    assertThat(model.containsAttribute("accounts"), is(true));
    assertThat(((Collection<Account>) model.asMap().get("accounts")).size(), is(1));
  }

}