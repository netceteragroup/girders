package com.netcetera.girders.autoconfigure.ldap;


import com.netcetera.girders.ldap.GirdersLdapMarkerInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration;
import org.springframework.boot.autoconfigure.ldap.LdapProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.DirContextAuthenticationStrategy;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.pool.factory.PoolingContextSource;
import org.springframework.ldap.pool.validation.DefaultDirContextValidator;

/**
 * Spring Boot auto configuration for the LDAP feature.
 */
@Slf4j
@ConditionalOnClass(GirdersLdapMarkerInterface.class)
@ConditionalOnMissingBean(LdapOperations.class)
@AutoConfigureBefore(LdapAutoConfiguration.class)
@EnableConfigurationProperties({LdapPoolingProperties.class, LdapProperties.class})
@Configuration
public class LdapTemplateAutoConfiguration {

  private final LdapPoolingProperties ldapPoolingProperties;
  private final LdapAutoConfiguration ldapAutoConfiguration;
  private final LdapProperties ldapProperties;
  private final Environment environment;
  private final ObjectProvider<DirContextAuthenticationStrategy> dirContextAuthenticationStrategy;

  /**
   * Constructor.
   *
   * @param ldapPoolingProperties Properties for the ldap pooling feature
   * @param ldapAutoConfiguration auto configuration of ldap by spring
   */
  public LdapTemplateAutoConfiguration(
      LdapPoolingProperties ldapPoolingProperties,
      LdapProperties ldapProperties,
      @Autowired LdapAutoConfiguration ldapAutoConfiguration,
      @Autowired Environment environment,
      @Autowired ObjectProvider<DirContextAuthenticationStrategy> dirContextAuthenticationStrategy) {

    this.ldapAutoConfiguration = ldapAutoConfiguration;
    this.ldapPoolingProperties = ldapPoolingProperties;
    this.ldapProperties = ldapProperties;
    this.environment = environment;
    this.dirContextAuthenticationStrategy = dirContextAuthenticationStrategy;
  }

  /**
   * @return the LDAP template used to access the LDAP server
   */
  @Bean
  public LdapTemplate ldapTemplate() {

    logger.info("Setting up ldapTemplate with a PoolingContextSource ");
    return new LdapTemplate(poolingContextSource());
  }

  /**
   * Returns the pooling context source providing pooled {@link javax.naming.directory.DirContext}s.
   * <p>
   * Wrapping the {@link LdapContextSource} in a {@link PoolingContextSource} is the recommended
   * pooling setup. See also {@link LdapContextSource#setPooled(boolean)}
   * </p>
   *
   * @return the pooling context source
   */
  @ConditionalOnMissingBean
  @Bean
  public PoolingContextSource poolingContextSource() {
    logger.info("Setting up poolingContextSource with properties '{}'.", ldapPoolingProperties);
    PoolingContextSource poolingContextSource = new PoolingContextSource();
    poolingContextSource.setMaxActive(ldapPoolingProperties.getMaxActive());
    poolingContextSource.setMaxTotal(ldapPoolingProperties.getMaxTotal());
    poolingContextSource.setMaxIdle(ldapPoolingProperties.getMaxIdle());
    poolingContextSource.setMinIdle(ldapPoolingProperties.getMinIdle());
    poolingContextSource.setMaxWait(ldapPoolingProperties.getMaxWait());
    poolingContextSource.setWhenExhaustedAction(ldapPoolingProperties.getWhenExhaustedAction());
    poolingContextSource.setTestOnBorrow(ldapPoolingProperties.isTestOnBorrow());
    poolingContextSource.setTestOnReturn(ldapPoolingProperties.isTestOnReturn());
    poolingContextSource.setTestWhileIdle(ldapPoolingProperties.isTestWhileIdle());
    poolingContextSource
        .setTimeBetweenEvictionRunsMillis(ldapPoolingProperties.getTimeBetweenEvictionRunsMillis());
    poolingContextSource
        .setNumTestsPerEvictionRun(ldapPoolingProperties.getNumTestsPerEvictionRun());
    poolingContextSource
        .setMinEvictableIdleTimeMillis(ldapPoolingProperties.getMinEvictableIdleTimeMillis());
    LdapContextSource contextSource =
        ldapAutoConfiguration.ldapContextSource(ldapProperties, environment, dirContextAuthenticationStrategy);
    contextSource.afterPropertiesSet();
    poolingContextSource.setContextSource(contextSource);
    poolingContextSource.setDirContextValidator(new DefaultDirContextValidator());
    return poolingContextSource;
  }
}
