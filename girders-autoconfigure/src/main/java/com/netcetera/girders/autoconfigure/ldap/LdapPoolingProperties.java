package com.netcetera.girders.autoconfigure.ldap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the LDAP pooling feature.
 */
@SuppressWarnings("ClassWithTooManyFields")
@ToString
@Getter
@Setter
@ConfigurationProperties(prefix = "girders.ldap-pool", ignoreUnknownFields = false)
public class LdapPoolingProperties {

  private int maxActive = 8;

  private int maxTotal = -1;

  private int maxIdle = 8;

  private int minIdle = 0;

  private long maxWait = 10000;

  private byte whenExhaustedAction = 1;

  private boolean testOnBorrow = false;

  private boolean testOnReturn = false;

  private boolean testWhileIdle = false;

  private long timeBetweenEvictionRunsMillis = -1;

  private int numTestsPerEvictionRun = 3;

  private long minEvictableIdleTimeMillis = 1800000;


}
