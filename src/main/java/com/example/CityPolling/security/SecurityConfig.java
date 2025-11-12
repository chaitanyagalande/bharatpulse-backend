package com.example.CityPolling.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {
    // Reference to our custom JWT filter
    private final JwtFilter jwtFilter;
    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * Bean for password encoding using BCrypt
     * BCrypt is secure and recommended for hashing passwords
     */

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Defines the main security filter chain
     * Controls which endpoints are public and which require authentication
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf((csrf -> csrf.disable()))
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS

                // Allow H2 console to be displayed in a frame
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

                // Define access rules for endpoints
                .authorizeHttpRequests(auth -> auth
                        // Allow public access to login and register endpoints
                        .requestMatchers("/h2-console/**").permitAll() // Allow H2 console
                        .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )

                // Configure session management
                // STATELESS because JWT tokens are stateless (no server-side session)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Add our custom JWT filter BEFORE Spring's default authentication filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        // Build and return the filter chain
        return http.build();
    }

    // CORRECT CORS CONFIGURATION
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*")); // Allow all origins
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
