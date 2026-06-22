package com.example.auction.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * What this file/class does in simple English:
 * This is the master control panel for the entire security system. It tells the application which URLs are public (like login and register) and which ones require a token. It also configures password scrambling and turns off "sessions" (since we are using tokens instead).
 *
 * Why it exists in this project:
 * Spring Security is very strict by default (it locks down everything). We need this file to open up the doors we want open, and to plug in our custom JWT filter.
 *
 * What would break if it was removed:
 * Every single request to our API would likely be blocked with a "401 Unauthorized" error, or we wouldn't be able to use our JWT tokens at all.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtRequestFilter;

    /**
     * What this method does in simple English:
     * Creates a tool that takes a normal password (like "password123") and turns it into a chaotic, irreversible string of characters (like "$2a$10$w...").
     *
     * Why it exists in this project:
     * It's extremely dangerous to store real passwords in a database. If a hacker steals the database, they get everyone's passwords. By storing scrambled versions (hashes), even if the database is stolen, the passwords remain safe.
     *
     * What would break if it was removed:
     * Passwords would be stored as plain text, creating a massive security vulnerability.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * What this method does in simple English:
     * Exposes the main "Authentication Manager" tool so we can use it in our login controller to actually trigger the password checking process.
     *
     * Why it exists in this project:
     * We need a way to say "Hey Spring, check this email and password for me."
     *
     * What would break if it was removed:
     * The login endpoint wouldn't be able to verify user credentials.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * What this method does in simple English:
     * This is the main rulebook. It states:
     * 1. Turn off CSRF (a type of web attack protection we don't need for APIs).
     * 2. Anyone can access "/api/auth/register" and "/api/auth/login" without being logged in.
     * 3. Every other request requires the user to be logged in (authenticated).
     * 4. Don't remember users between requests (stateless) because we use JWTs.
     * 5. Make sure our custom JwtAuthenticationFilter runs BEFORE the default security checks.
     *
     * Why it exists in this project:
     * To wire all the pieces together and define the actual security policy of the app.
     *
     * What would break if it was removed:
     * The security system wouldn't know how to behave, making the app either completely locked down or totally insecure.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
            .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless APIs
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/register", "/api/auth/login", "/ws/**", "/api/auctions/**", "/actuator/**").permitAll() // Public endpoints
                .anyRequest().authenticated() // Everything else requires login
            )
            .sessionManagement(sess -> sess
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Don't use sessions, use JWT
            );

        // Add our JWT filter to the security chain
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * What this method does in simple English:
     * This is the "Bouncer" for cross-origin requests. It explicitly tells the backend: "It is safe to accept requests coming from http://localhost:3000 (our React frontend)."
     *
     * Why it exists in this project:
     * Browsers have a strict security rule called CORS. By default, they block a website on one port (3000) from talking to a server on another port (8080) to prevent malicious scripts. We have to explicitly whitelist our frontend port.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
