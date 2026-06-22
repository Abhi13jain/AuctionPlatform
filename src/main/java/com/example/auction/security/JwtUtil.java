package com.example.auction.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;

/**
 * What this file/class does in simple English:
 * This class is our "ID Card Maker and Verifier". It is responsible for creating JWTs (JSON Web Tokens) when a user logs in, and later checking if those tokens are real and haven't expired when the user tries to do something.
 *
 * Why it exists in this project:
 * Because our application is "stateless" (it doesn't remember users between requests), the user must send an ID card (token) with every request. This class handles all the complex math and rules for generating and reading those cards securely.
 *
 * What would break if it was removed:
 * We couldn't create login tokens, and we couldn't verify returning users, completely breaking the login system.
 */
@Component
public class JwtUtil {

    /**
     * What this field does in simple English:
     * This is the master secret key used to sign the tokens. Only our server knows it. It's like a special seal on a document.
     *
     * Why it exists in this project:
     * To prevent bad guys from creating fake ID cards. If they don't know the secret, they can't create a valid token.
     *
     * What would break if it was removed:
     * We couldn't securely sign our tokens, meaning anyone could pretend to be anyone else.
     */
    private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /**
     * What this method does in simple English:
     * It looks at a token and extracts the user's username (which in our case is their email).
     *
     * Why it exists in this project:
     * When a user makes a request with a token, we need to know who they claim to be so we can look them up in the database.
     *
     * What would break if it was removed:
     * We would receive tokens but have no idea who they belong to.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * What this method does in simple English:
     * Checks what time the token is set to expire.
     *
     * Why it exists in this project:
     * Tokens shouldn't last forever. If someone steals a token, we want it to become useless eventually.
     *
     * What would break if it was removed:
     * We couldn't enforce time limits on logins.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * What this method does in simple English:
     * A helper method to extract specific pieces of information (called "claims") from the token.
     *
     * Why it exists in this project:
     * It reduces repeated code when we need to pull out the username, expiration date, or other details.
     *
     * What would break if it was removed:
     * The other extraction methods (like extractUsername) would stop working.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * What this method does in simple English:
     * It opens up the token using our secret key to read all the information inside it.
     *
     * Why it exists in this project:
     * This is the core method that actually reads the token. It also throws an error if the token was tampered with (because the signature won't match the secret key).
     *
     * What would break if it was removed:
     * We wouldn't be able to read any tokens.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
    }

    /**
     * What this method does in simple English:
     * Checks if the token's expiration date has passed.
     *
     * Why it exists in this project:
     * To ensure we don't accept old, expired tokens.
     *
     * What would break if it was removed:
     * Users would never be logged out automatically; their tokens would work forever.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * What this method does in simple English:
     * Creates a brand new token for a user who just successfully logged in.
     *
     * Why it exists in this project:
     * It's the final step of the login process. It generates the digital ID card to give to the user.
     *
     * What would break if it was removed:
     * Users would log in successfully but wouldn't receive a token to use for their next requests.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Inject the user's roles into the JWT token so the React frontend can read them
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * What this method does in simple English:
     * The actual builder of the token. It sets the username, the time it was created, the expiration time (e.g., 10 hours from now), and signs it with our secret key.
     *
     * Why it exists in this project:
     * To handle the specific details of token construction.
     *
     * What would break if it was removed:
     * The generateToken method wouldn't work.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * What this method does in simple English:
     * Checks if a token is completely valid. It verifies two things: 1) the username in the token matches the user trying to use it, and 2) it's not expired.
     *
     * Why it exists in this project:
     * Every time a user makes a protected request, we use this to double-check their token is good to go.
     *
     * What would break if it was removed:
     * We wouldn't know if a token was legitimate, allowing anyone to bypass security.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
