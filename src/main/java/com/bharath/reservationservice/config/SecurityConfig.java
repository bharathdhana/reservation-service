package com.bharath.reservationservice.config;

import com.bharath.reservationservice.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Create reservation
                        .requestMatchers(HttpMethod.POST, "/api/reservation/**")
                        .hasAnyRole("USER", "ADMIN")

                        // Admin -> all reservations
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/reservation"
                        )
                        .hasRole("ADMIN")

                        // Admin -> reservations by show
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/reservation/show/**"
                        )
                        .hasRole("ADMIN")

                        // User & Admin -> view user reservations
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/reservation/user/**"
                        )
                        .hasAnyRole("USER", "ADMIN")

                        // User -> reservation by ID
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/reservation/**"
                        )
                        .hasAnyRole("USER", "ADMIN")

                        // User -> cancel reservation
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/reservation/**"
                        )
                        .hasAnyRole("USER", "ADMIN")

                        // IMPORTANT:
                        // Do NOT allow USER to DELETE
                        // DELETE should be ADMIN only
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/reservation/**"
                        )
                        .hasRole("ADMIN")
                        .anyRequest()
                        .authenticated()
                )

                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                );
        return http.build();
    }
}