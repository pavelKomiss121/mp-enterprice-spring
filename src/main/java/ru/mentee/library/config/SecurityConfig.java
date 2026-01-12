/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers(HttpMethod.GET, "/api/books")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.POST, "/api/books")
                                        .hasAnyRole("LIBRARIAN", "ADMIN")
                                        .requestMatchers(HttpMethod.PUT, "/api/books/**")
                                        .hasAnyRole("LIBRARIAN", "ADMIN")
                                        .requestMatchers(HttpMethod.DELETE, "/api/books/**")
                                        .hasAnyRole("LIBRARIAN", "ADMIN")
                                        .requestMatchers("/api/users/**")
                                        .hasRole("ADMIN")
                                        .anyRequest()
                                        .authenticated())
                .userDetailsService(userDetailsService)
                .httpBasic(httpBasic -> {});

        return http.build();
    }
}
