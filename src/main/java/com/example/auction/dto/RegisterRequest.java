package com.example.auction.dto;

/**
 * What this file/class does in simple English:
 * This class is a "Data Transfer Object" (DTO). It acts like an envelope that carries the information a new user types into the registration form (name, email, password) from their browser to our server.
 *
 * Why it exists in this project:
 * We don't want to expose our actual database `User` class directly to the outside world. Using a DTO gives us a safe, controlled way to accept incoming data.
 *
 * What would break if it was removed:
 * Our registration endpoint wouldn't know how to read the information the user is sending, breaking the sign-up process.
 */
public class RegisterRequest {

    private String name;
    private String email;
    private String password;

    /**
     * What this method does in simple English:
     * Gets the name the user typed in.
     *
     * Why it exists in this project:
     * So we can read it and save it to the database.
     *
     * What would break if it was removed:
     * The server wouldn't be able to read the name.
     */
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    /**
     * What this method does in simple English:
     * Gets the email the user typed in.
     *
     * Why it exists in this project:
     * So we can read it, check if it's taken, and save it.
     *
     * What would break if it was removed:
     * The server wouldn't be able to read the email.
     */
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    /**
     * What this method does in simple English:
     * Gets the raw password the user typed in.
     *
     * Why it exists in this project:
     * So we can read it, scramble (hash) it, and save the scrambled version.
     *
     * What would break if it was removed:
     * The server wouldn't be able to read the password.
     */
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
