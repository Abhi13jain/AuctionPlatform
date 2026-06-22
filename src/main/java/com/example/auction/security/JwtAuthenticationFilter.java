package com.example.auction.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * What this file/class does in simple English:
 * This acts like a security guard standing at the front door of our application. Every single time a request comes in (like trying to view items or place a bid), this guard checks if the user is carrying a valid ID card (a JWT token) in their request.
 *
 * Why it exists in this project:
 * To automatically authenticate users who have already logged in. If they have a valid token, we tell Spring Security "This user is good, let them through," so they don't have to send their email and password with every single click.
 *
 * What would break if it was removed:
 * Our application would completely ignore tokens. Users might log in successfully, but the very next thing they click would say "Access Denied" because the app forgot who they were.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * What this method does in simple English:
     * This is the actual code the security guard runs. It grabs the "Authorization" header from the request, pulls out the token, reads the username from it, checks if it's valid, and if so, logs the user in for this specific request.
     *
     * Why it exists in this project:
     * It's the core logic for processing JWTs on incoming web traffic.
     *
     * What would break if it was removed:
     * The security filter wouldn't do anything, breaking all authenticated actions.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Look for the "Authorization" header in the incoming request
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // 2. Check if the header exists and starts with "Bearer " (the standard format for JWTs)
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Cut off the "Bearer " part to get just the token string
            jwt = authorizationHeader.substring(7);
            try {
                // Use our JwtUtil to extract the username (email) from the token
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                // If the token is tampered with, expired, or the server restarted (new secret key),
                // this will catch the exception and simply ignore the invalid token instead of crashing with a 500 Error.
                System.out.println("Invalid JWT Token: " + e.getMessage());
            }
        }

        // 3. If we found a username, and the user isn't already logged into the current security context...
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load their full details from the database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 4. Double check that the token is completely valid
            if (jwtUtil.validateToken(jwt, userDetails)) {
                // 5. Tell Spring Security: "This user is authenticated for this request. Let them pass."
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        
        // 6. Continue processing the request (pass it to the next guard or the final destination)
        filterChain.doFilter(request, response);
    }
}
