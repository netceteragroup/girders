package com.netcetera.girders.demo.adminserver;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * {@link Configuration} for the Spring Boot Admin Server application.
 */
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  private String adminContextPath;

  /**
   * Constructor for Spring Boot Admin Server security configuration.
   *
   * @param adminServerProperties configuration properties for the Spring Boot Admin application
   */
  public SecurityConfiguration(AdminServerProperties adminServerProperties) {
    adminContextPath = adminServerProperties.getContextPath();
  }

  /**
   * Create an instance of a user details service.
   *
   * @return Created and initialized bean
   */
  @Bean
  public UserDetailsService userDetailsService() {
    UserDetailsManager manager = new InMemoryUserDetailsManager();
    manager.createUser(User.withDefaultPasswordEncoder().username("admin").password("admin").roles("ADMIN").build());
    manager.createUser(
        User.withDefaultPasswordEncoder().username("monitoring").password("monitoring").roles("MONITORING").build());
    return manager;
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
    successHandler.setTargetUrlParameter("redirectTo");
    successHandler.setDefaultTargetUrl(adminContextPath + "/");

    // Allow static content and login page
    http.authorizeRequests()
        .antMatchers(adminContextPath + "/assets/**").permitAll()
        .antMatchers(adminContextPath + "/login").permitAll()
        .antMatchers(adminContextPath + "/logout").permitAll();

    // Authorize the Spring Boot Admin Client registration endpoint to be accessed only with user in MONITORING role
    // Authorize the Spring Boot Admin UI endpoints to be accessed only with user in ADMIN role
    http.authorizeRequests()
        .antMatchers(adminContextPath + "/instances").hasAnyRole("MONITORING")
        .antMatchers(adminContextPath + "/**").hasAnyRole("ADMIN");

    // setup authentication
    http.authorizeRequests().anyRequest().authenticated()
        .and()
        // Configure login and logout
        .formLogin().loginPage(adminContextPath + "/login").successHandler(successHandler)
        .and()
        .logout().logoutUrl(adminContextPath + "/logout")
        // Allow Http Basic authentication, used by the Spring Boot Admin Client applications to register
        .and().httpBasic().and()
        // Enable CSRF using Cookies except for actuator endpoints, logout endpoint
        // and instances endpoint where SBA Client registers
        .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .ignoringAntMatchers("/instances", "/actuator/**", "/logout");
  }
}
