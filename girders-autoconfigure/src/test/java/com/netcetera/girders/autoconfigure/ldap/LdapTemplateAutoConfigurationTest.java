package com.netcetera.girders.autoconfigure.ldap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.DirContextAuthenticationStrategy;
import org.springframework.ldap.pool.factory.PoolingContextSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ImportAutoConfiguration({LdapTemplateAutoConfiguration.class, LdapAutoConfiguration.class})
@ExtendWith(SpringExtension.class)
class LdapTemplateAutoConfigurationTest {

  @Autowired
  private LdapTemplate ldapTemplate;

  @Autowired
  private ContextSource ldapContextSource;

  @Autowired
  private ObjectProvider<DirContextAuthenticationStrategy> dirContextAuthenticationStrategiesProvider;


  @Test
  void shouldUsePooledContextSource() {

    assertNotNull(ldapContextSource);
    assertNotNull(ldapTemplate);
    assertNotNull(dirContextAuthenticationStrategiesProvider);
    assertThat(ldapTemplate.getContextSource(), is(instanceOf(PoolingContextSource.class)));
  }


}
