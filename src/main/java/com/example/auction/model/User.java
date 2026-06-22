package com.example.auction.model;

import jakarta.persistence.*;

/**
 * What this file/class does in simple English:
 * This class represents a "User" in our database. It acts as a blueprint, telling the database to create a table (named "users") with columns for id, name, email, password, and their role (BUYER or ADMIN).
 *
 * Why it exists in this project:
 * Whenever someone registers or logs in, we need a way to store and retrieve their information from the database in a format that our Java code can understand. This class acts as the bridge between our Java code and the database.
 *
 * What would break if it was removed:
 * The application would have no way to store or load user data. Login, registration, and basically the entire authentication system would completely stop working.
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * What this field does in simple English:
     * This is the unique identifier (ID) for each user. The database will automatically generate a new, unique number for every new user.
     *
     * Why it exists in this project:
     * We need a foolproof way to tell users apart, even if they have the same name or change their email.
     *
     * What would break if it was removed:
     * We couldn't uniquely identify users, which would cause chaos when updating profiles or linking users to their actions (like placing a bid).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * What this field does in simple English:
     * Stores the user's full name.
     *
     * Why it exists in this project:
     * To personalize the experience and know who is using the platform.
     *
     * What would break if it was removed:
     * We wouldn't know the names of our users, making the app feel very robotic.
     */
    private String name;

    /**
     * What this field does in simple English:
     * Stores the user's email address. It must be unique (no two users can have the same email).
     *
     * Why it exists in this project:
     * Email is used as the username for logging in and for sending notifications.
     *
     * What would break if it was removed:
     * Users wouldn't be able to log in, because the login process relies on checking their email.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * What this field does in simple English:
     * Stores the user's secret password. We never store the real password; we store a "hashed" (scrambled and unreadable) version of it for security.
     *
     * Why it exists in this project:
     * To verify a user's identity when they try to log in.
     *
     * What would break if it was removed:
     * Anyone could log in as anyone else because there would be no passwords to check.
     */
    @Column(nullable = false)
    private String password;

    /**
     * What this field does in simple English:
     * Stores whether the user is a normal "BUYER" or an "ADMIN" with special privileges.
     *
     * Why it exists in this project:
     * To control what a user is allowed to do in the system.
     *
     * What would break if it was removed:
     * We couldn't restrict access to certain features, meaning regular users might be able to do admin things.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Default constructor for JPA
    public User() {}

    /**
     * What this method does in simple English:
     * Gets the user's ID.
     *
     * Why it exists in this project:
     * To retrieve the unique ID when we need to find this user in the database.
     *
     * What would break if it was removed:
     * Other parts of the code wouldn't be able to access the user's ID.
     */
    public Long getId() { return id; }

    /**
     * What this method does in simple English:
     * Sets the user's ID.
     *
     * Why it exists in this project:
     * Allows the database framework to assign an ID to a new user.
     *
     * What would break if it was removed:
     * The framework might fail to properly save and load the user ID.
     */
    public void setId(Long id) { this.id = id; }

    /**
     * What this method does in simple English:
     * Gets the user's name.
     *
     * Why it exists in this project:
     * To retrieve the name to display on the screen.
     *
     * What would break if it was removed:
     * We couldn't display the user's name.
     */
    public String getName() { return name; }

    /**
     * What this method does in simple English:
     * Sets the user's name.
     *
     * Why it exists in this project:
     * To give a new user a name when they register.
     *
     * What would break if it was removed:
     * We couldn't save a user's name during registration.
     */
    public void setName(String name) { this.name = name; }

    /**
     * What this method does in simple English:
     * Gets the user's email.
     *
     * Why it exists in this project:
     * To retrieve the email for login or sending messages.
     *
     * What would break if it was removed:
     * Login process couldn't retrieve the email to check it.
     */
    public String getEmail() { return email; }

    /**
     * What this method does in simple English:
     * Sets the user's email.
     *
     * Why it exists in this project:
     * To assign an email when a user registers.
     *
     * What would break if it was removed:
     * We couldn't save the email during registration.
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * What this method does in simple English:
     * Gets the user's hashed password.
     *
     * Why it exists in this project:
     * The security system needs this to compare it against the password the user typed in (after hashing it).
     *
     * What would break if it was removed:
     * The security system wouldn't be able to check if the password is correct.
     */
    public String getPassword() { return password; }

    /**
     * What this method does in simple English:
     * Sets the user's password.
     *
     * Why it exists in this project:
     * To store the scrambled password when a user registers.
     *
     * What would break if it was removed:
     * We couldn't save a password during registration.
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * What this method does in simple English:
     * Gets the user's role (e.g., BUYER or ADMIN).
     *
     * Why it exists in this project:
     * The security system needs this to know what permissions the user has.
     *
     * What would break if it was removed:
     * The security system wouldn't know if the user is an admin or not.
     */
    public Role getRole() { return role; }

    /**
     * What this method does in simple English:
     * Sets the user's role.
     *
     * Why it exists in this project:
     * To assign a role (usually BUYER by default) when someone registers.
     *
     * What would break if it was removed:
     * We couldn't assign roles to users.
     */
    public void setRole(Role role) { this.role = role; }
}
