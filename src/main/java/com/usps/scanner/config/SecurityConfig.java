package com.usps.scanner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// @Configuration = this class defines Spring beans (shared singletons available app-wide)
@Configuration
public class SecurityConfig {

    // The JWT filter is a Spring @Component, so Spring injects it here
    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    // SecurityFilterChain controls how every HTTP request is filtered
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF — CSRF tokens are for browser sessions, not stateless JWT APIs
            .csrf(csrf -> csrf.disable())
            // STATELESS = no server-side session — JWT carries auth on each request
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Lock down endpoints — public ones listed first, everything else requires JWT
            // NOTE: /api/users and /api/scores are temporarily public because the mail carrier game
            // (legacy client) doesn't send JWTs yet. Lock these down once the game is updated.
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()        // login/register
                .requestMatchers("/api/health").permitAll()         // health check
                .requestMatchers("/api/leaderboard").permitAll()    // public leaderboard
                .requestMatchers("/api/users/**").permitAll()       // legacy game client
                .requestMatchers("/api/scores").permitAll()         // legacy game client
                .requestMatchers("/api/inspect").permitAll()        // legacy mobile scanner client
                .requestMatchers("/api/history").permitAll()        // legacy mobile scanner client
                .requestMatchers("/api/carriers/**").permitAll()    // postmaster dashboard read-only feed
                .requestMatchers("/", "/index.html", "/leaderboard.html", "/supervisor.html", "/postmaster.html", "/history.html", "/*.css", "/*.js").permitAll()  // static front-end pages
                .requestMatchers("/h2-console/**").permitAll()      // local SQL playground for learning
                .anyRequest().authenticated()                       // everything else requires JWT
            )
            // H2 console uses HTML frames — allow them so the console UI works
            .headers(h -> h.frameOptions(f -> f.disable()))
            // Run our JWT filter BEFORE Spring's default username/password filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // BCryptPasswordEncoder hashes passwords with a salted one-way function
    // Used by AuthController to hash on register and verify on login
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
