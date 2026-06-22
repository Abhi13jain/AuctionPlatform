package com.example.auction.controller;

import com.example.auction.dto.AuthRequest;
import com.example.auction.dto.AuthResponse;
import com.example.auction.dto.RegisterRequest;
import com.example.auction.model.Role;
import com.example.auction.model.User;
import com.example.auction.repository.UserRepository;
import com.example.auction.security.CustomUserDetailsService;
import com.example.auction.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * What this file/class does in simple English:
 * This is the "Receptionist" for our authentication system. It listens for incoming web requests from the outside world specifically related to signing up or logging in. When a request arrives, it directs it to the right place and sends the response back to the user.
 *
 * Why it exists in this project:
 * The frontend (like a React app or mobile app) needs specific URLs (endpoints) it can send data to. This controller creates those URLs (like /api/auth/login) and connects them to our internal Java code.
 *
 * What would break if it was removed:
 * Users would have no way to reach our registration or login logic from the internet.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * What this method does in simple English:
     * This method handles new user sign-ups. It takes the registration envelope (name, email, password), checks if the email is already used, scrambles the password for safety, saves the new user to the database, and sends back a success message.
     *
     * Why it exists in this project:
     * To allow new people to join our platform.
     *
     * What would break if it was removed:
     * No new users could ever sign up for the application.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        
        // 1. Check if a user with this email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already taken!");
        }

        // 2. Create a new blank User profile
        User newUser = new User();
        newUser.setName(registerRequest.getName());
        newUser.setEmail(registerRequest.getEmail());
        
        // 3. Scramble (hash) the password before saving it
        String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());
        newUser.setPassword(hashedPassword);
        
        // 4. Assign the default role (BUYER)
        newUser.setRole(Role.BUYER);

        // 5. Save the new user to the database
        userRepository.save(newUser);

        return ResponseEntity.ok("User registered successfully!");
    }

    /**
     * What this method does in simple English:
     * This method handles logins. It takes the login envelope (email and password), asks Spring Security to verify them, and if they are correct, it prints out a brand new JWT "ID card" and gives it to the user.
     *
     * Why it exists in this project:
     * To allow existing users to prove who they are and get the token they need to use the app.
     *
     * What would break if it was removed:
     * Existing users wouldn't be able to log in and get their access tokens.
     */
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {

        try {
            // 1. Ask the AuthenticationManager to check the email and password
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            // If the check fails (wrong email or password), throw an error
            throw new Exception("Incorrect username or password", e);
        }

        // 2. If we get here, the login was successful. Load the user's details.
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authRequest.getEmail());

        // 3. Use the details to create a new JWT token
        final String jwt = jwtUtil.generateToken(userDetails);

        // 4. Send the token back to the user inside an AuthResponse envelope
        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}
