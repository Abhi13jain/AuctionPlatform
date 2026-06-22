package com.example.auction.dto;

/**
 * What this file/class does in simple English:
 * This class is the envelope we send *back* to the user after they successfully log in or register. It contains their digital "key card" (the JWT token).
 *
 * Why it exists in this project:
 * The frontend (like a React or mobile app) needs this token to prove who the user is on future requests. This class is how we send that token to them.
 *
 * What would break if it was removed:
 * Even if login was successful, the user wouldn't receive their token, meaning they couldn't actually access any protected parts of the application.
 */
public class AuthResponse {

    private String token;

    /**
     * What this method does in simple English:
     * This is a "constructor", a special method that creates a new AuthResponse envelope and immediately puts the token inside it.
     *
     * Why it exists in this project:
     * It's a convenient way to create this object in one line of code.
     *
     * What would break if it was removed:
     * Creating this response would require more lines of code.
     */
    public AuthResponse(String token) {
        this.token = token;
    }

    /**
     * What this method does in simple English:
     * Gets the token from the response.
     *
     * Why it exists in this project:
     * The framework that converts our Java object into a format the web browser understands (JSON) needs this method to read the token.
     *
     * What would break if it was removed:
     * The token wouldn't be included in the response sent to the browser.
     */
    public String getToken() { return token; }
    
    public void setToken(String token) { this.token = token; }
}
