package com.training.on_class.infrastructure.entrypoints.rest.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
          .csrf(ServerHttpSecurity.CsrfSpec::disable)
          .authorizeExchange(exchanges -> exchanges
            .pathMatchers(HttpMethod.POST, "/api/v1/capacities").hasRole("ADMIN")
            .pathMatchers(HttpMethod.GET, "/api/v1/capacities").hasRole("ADMIN")
            .anyExchange().permitAll()
          )
          .httpBasic(withDefaults())
          .build();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails admin = User.withUsername("admin")
          .password("{noop}admin123")
          .roles("ADMIN")
          .build();

        UserDetails persona = User.withUsername("persona")
          .password("{noop}persona123")
          .roles("PERSONA")
          .build();

        return new MapReactiveUserDetailsService(admin, persona);
    }
}
