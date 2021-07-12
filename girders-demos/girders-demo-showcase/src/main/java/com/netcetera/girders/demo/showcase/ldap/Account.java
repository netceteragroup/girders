package com.netcetera.girders.demo.showcase.ldap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data object for a user account.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {

  private String uid;
  private String firstName;
  private String lastName;
  private String email;

}
