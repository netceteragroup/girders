package com.netcetera.girders.demo.adminserver;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * {@link Configuration} for the Spring Boot Admin Server application.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

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
    manager.createUser(User.withDefaultPasswordEncoder()
        .username("admin")
        .password("admin")
        .roles("ADMIN")
        .build());
    manager.createUser(User.withDefaultPasswordEncoder()
        .username("monitoring")
        .password("monitoring")
        .roles("MONITORING")
        .build());
    return manager;
  }

  @SuppressWarnings("checkstyle:IllegalThrows")
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
    successHandler.setTargetUrlParameter("redirectTo");
    successHandler.setDefaultTargetUrl(adminContextPath + '/');

    // Allow static content and login page
    http.authorizeHttpRequests(
        requests -> requests
            .requestMatchers(new AntPathRequestMatcher(adminContextPath + "/assets/**"),
                new AntPathRequestMatcher(adminContextPath + "/variables.css"),
                new AntPathRequestMatcher(adminContextPath + "/login"),
                new AntPathRequestMatcher(adminContextPath + "/logout"))
            .permitAll());
    
    http.authorizeHttpRequests(requests -> requests.dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll());

    // Authorize the Spring Boot Admin Client registration endpoint to be accessed only with user in
    // MONITORING role
    // Authorize the Spring Boot Admin UI endpoints to be accessed only with user in ADMIN role
    http.authorizeHttpRequests(requests -> requests
        .requestMatchers(new AntPathRequestMatcher(adminContextPath + "/instances"))
        .hasAnyRole("MONITORING"));
    http.authorizeHttpRequests(
        requests -> requests.requestMatchers(new AntPathRequestMatcher(adminContextPath + "/**"))
            .hasAnyRole("ADMIN"));

    // setup authentication
    http.authorizeHttpRequests(
        requests -> requests.anyRequest().authenticated());
    http.formLogin(loginConfigurer -> {
      loginConfigurer.loginPage(adminContextPath + "/login");
      loginConfigurer.successHandler(successHandler);
    });
    http.logout(configurer -> configurer.logoutUrl(adminContextPath + "/logout"));

    http.httpBasic(configurer -> configurer.realmName("Spring Boot Admin Server"));
    http.csrf(csrfConfigurer -> {
      csrfConfigurer.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
      csrfConfigurer.ignoringRequestMatchers(
          new AntPathRequestMatcher(adminContextPath + "/instances"),
          new AntPathRequestMatcher(adminContextPath + "/actuator/**"),
          new AntPathRequestMatcher(adminContextPath + "/logout")
      );

    });
    return http.build();
  }
}
