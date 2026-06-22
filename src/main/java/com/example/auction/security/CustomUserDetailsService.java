package com.example.auction.security;

import com.example.auction.model.User;
import com.example.auction.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

/**
 * What this file/class does in simple English:
 * This class is a helper for Spring Security. It tells Spring Security *how* to find a user in our specific database when someone tries to log in.
 *
 * Why it exists in this project:
 * Spring Security is a generic security tool. It doesn't know we have a "UserRepository" or a "User" class with emails. We have to create this specific service to connect Spring Security to our specific database setup.
 *
 * What would break if it was removed:
 * Spring Security wouldn't be able to look up users in our database, making login impossible.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * What this field does in simple English:
     * This brings in our UserRepository so we can search the database.
     *
     * Why it exists in this project:
     * We need it to run the findByEmail() method.
     *
     * What would break if it was removed:
     * We wouldn't have access to the database here.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * What this method does in simple English:
     * It takes an email address (which Spring calls a "username"), looks it up in our database, and converts our `User` object into a standard `UserDetails` object that Spring Security understands.
     *
     * Why it exists in this project:
     * This is the specific method Spring Security automatically calls when it needs to verify a login.
     *
     * What would break if it was removed:
     * The login process would crash because Spring Security wouldn't know how to fetch user details.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Find the user in our database using their email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Convert our custom User into Spring Security's standard User object
        // We pass the email, password, and their specific role so @PreAuthorize can check it.
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
