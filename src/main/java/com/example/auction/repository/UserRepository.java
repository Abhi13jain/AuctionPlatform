package com.example.auction.repository;

import com.example.auction.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * What this file/class does in simple English:
 * This interface is like a magic translator. We tell it "we want to find a User by their email," and Spring Boot automatically writes the complex database SQL query to do that for us.
 *
 * Why it exists in this project:
 * We need a way to talk to the database to save new users or find existing ones. Extending JpaRepository gives us a bunch of free, ready-to-use methods like save(), findById(), and delete() without writing any actual database code.
 *
 * What would break if it was removed:
 * Our Java code would have no way to communicate with the database table that holds the users.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * What this method does in simple English:
     * It searches the database for a user that has a specific email address. It returns an "Optional", which is like a box that might contain a User, or might be empty (if no one with that email exists).
     *
     * Why it exists in this project:
     * When someone tries to log in, we only have their email. We use this method to find their full profile (including their scrambled password) to verify who they are.
     *
     * What would break if it was removed:
     * The login system wouldn't be able to find users, making it impossible for anyone to log in.
     */
    Optional<User> findByEmail(String email);
    
    /**
     * What this method does in simple English:
     * It checks if a user with a specific email already exists in the database. Returns true if yes, false if no.
     *
     * Why it exists in this project:
     * When someone registers, we must ensure they aren't using an email that is already taken by someone else.
     *
     * What would break if it was removed:
     * Multiple people could register with the exact same email address, breaking the login process and confusing the system.
     */
    boolean existsByEmail(String email);
}
