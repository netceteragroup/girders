package com.netcetera.girders.demo.showcase.ldap;

import org.springframework.LdapDataEntry;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.stereotype.Component;

/**
 * {@link ContextMapper} for LDAP accounts.
 */
@Component
public class AccountAttributesMapper implements ContextMapper<Account> {

  @Override
  public Account mapFromContext(Object ctx) {
    LdapDataEntry adapter = (LdapDataEntry) ctx;
    Account account = new Account();
    account.setUid(adapter.getStringAttribute("uid"));
    account.setFirstName(adapter.getStringAttribute("cn"));
    account.setLastName(adapter.getStringAttribute("sn"));
    account.setEmail(adapter.getStringAttribute("mail"));
    return account;
  }

}
