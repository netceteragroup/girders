package com.netcetera.girders.demo.showcase;

import com.netcetera.girders.autoconfigure.csp.CspProperties;
import com.netcetera.girders.csrf.CsrfTokenCookieBindingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.header.writers.ContentSecurityPolicyHeaderWriter;

/**
 * {@link Configuration} for the security layer.
 */
@Configuration
@EnableWebSecurity
@ComponentScan("com.netcetera.girders.autoconfigure.*")
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration {


  private static final String ROLE_ADMIN = "ADMIN";
  private static final String ROLE_MONITORING = "MONITORING";

  private final CspProperties cspProperties;
  private final ContentSecurityPolicyHeaderWriter cspHeaderWriter;

  /**
   * Constructor.
   *
   * @param cspProperties   Properties for CSP
   * @param cspHeaderWriter Writer for the CSP header
   */
  public SecurityConfiguration(CspProperties cspProperties, ContentSecurityPolicyHeaderWriter cspHeaderWriter) {
    this.cspProperties = cspProperties;
    this.cspHeaderWriter = cspHeaderWriter;
  }

  /**
   * Create an instance of a user details service.
   *
   * @return Created and initialized bean
   */
  @Bean
  public UserDetailsService userDetailsService() {
    UserDetailsManager manager = new InMemoryUserDetailsManager();
    manager.createUser(
        User.withDefaultPasswordEncoder().username("user").password("user").roles("USER").build());
    manager.createUser(User.withDefaultPasswordEncoder()
        .username("admin")
        .password("admin")
        .roles("ADMIN", "USER")
        .build());
    manager.createUser(User.withDefaultPasswordEncoder()
        .username("monitoring")
        .password("monitoring")
        .roles("MONITORING")
        .build());
    return manager;
  }

  // CHECKSTYLE:OFF

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    // Configure CSP and CSRF
    configureForCspFeature(http);
    configureForCsrfFeature(http);

    // Enable both HTTP Basic Auth (for REST clients)
    http.httpBasic().realmName("Girders Demo Application");

    // Enable form login for browser users
    http.formLogin().loginPage("/login").permitAll();
    http.logout().logoutUrl("/logout").permitAll();

    // Allow access to public content (HTML assets, log resource)
    http.authorizeHttpRequests()
        .requestMatchers("/webjars/**", "/styles/**", "/js/**", "/images/**")
        .permitAll();
    http.authorizeHttpRequests().requestMatchers("/log").permitAll();

    // Restrict access to actuator endpoints to users with admin or monitoring roles
    http.authorizeHttpRequests()
        .requestMatchers("/actuator/**")
        .hasAnyRole(ROLE_ADMIN, ROLE_MONITORING);

    // Open access to everything else, i.e. the application itself
    return http.authorizeHttpRequests().anyRequest().permitAll().and().build();
  }

  @SuppressWarnings("ProhibitedExceptionDeclared")
  private void configureForCspFeature(HttpSecurity http) throws Exception {
    // enable CSP protection by adding the auto configured header writer
    http.headers().addHeaderWriter(cspHeaderWriter);
  }

  @SuppressWarnings({"rawtypes", "ProhibitedExceptionDeclared"})
  private void configureForCsrfFeature(HttpSecurity http) throws Exception {
    http.addFilterAfter(new CsrfTokenCookieBindingFilter(), CsrfFilter.class);

    // disable CSRF protection for the CSP violation report endpoint since the
    // browser will do POSTs w/o a CSRF token in the request parameters and HTTP
    // headers
    http.csrf().ignoringRequestMatchers(cspProperties.getReportUrl());

    // disable CSRF protection for the logout endpoint
    http.csrf().ignoringRequestMatchers("/logout");
  }

  // CHECKSTYLE:ON

}
