package com.example.auction.model;

/**
 * What this file/class does in simple English:
 * This file defines the different types of users that can exist in our system. Right now, a user can either be a "BUYER" or an "ADMIN".
 *
 * Why it exists in this project:
 * We need a standard, consistent way to represent user roles. By using an "Enum" (a special Java type that only allows specific predefined values), we prevent mistakes like someone typing "Admin" or "administrator" instead of "ADMIN".
 *
 * What would break if it was removed:
 * Our application wouldn't know how to distinguish between a regular buyer and an administrator, making it impossible to restrict sensitive features to admins only.
 */
public enum Role {
    BUYER,
    ADMIN
}
