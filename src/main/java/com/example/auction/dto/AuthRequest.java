package com.example.auction.dto;

/**
 * What this file/class does in simple English:
 * This class is an envelope that carries the user's login credentials (email and password) from their browser to our server when they try to log in.
 *
 * Why it exists in this project:
 * We need a structured way to receive the login information.
 *
 * What would break if it was removed:
 * Users wouldn't be able to log in, because our server wouldn't know how to understand their login requests.
 */
public class AuthRequest {

    private String email;
    private String password;

    /**
     * What this method does in simple English:
     * Gets the email from the login attempt.
     *
     * Why it exists in this project:
     * So we can look up the user in the database.
     *
     * What would break if it was removed:
     * We wouldn't know who is trying to log in.
     */
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    /**
     * What this method does in simple English:
     * Gets the password from the login attempt.
     *
     * Why it exists in this project:
     * So we can check if it matches their actual password.
     *
     * What would break if it was removed:
     * We couldn't verify if they entered the correct password.
     */
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
