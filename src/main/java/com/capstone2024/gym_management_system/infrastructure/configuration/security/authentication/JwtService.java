package com.capstone2024.gym_management_system.infrastructure.configuration.security.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public final class JwtService {

    @Value("${jwt.access_token.lifetime}")
    private Long accessTokenLifetime;

    @Value("${jwt.token.secret}")
    private String tokenSecret;

    @Value("${jwt.token.issuer}")
    private String tokenIssuer;

    public String generateToken(UserDetails userDetails) {
        Validate.notNull(userDetails, "User details is null when generating JWT token.");
        Instant issuedAt = Instant.now();
        return JWT.create()
                .withIssuer(tokenIssuer)
                .withSubject(userDetails.getUsername())
                .withIssuedAt(issuedAt)
                .withExpiresAt(issuedAt.plusMillis(accessTokenLifetime))
                .sign(Algorithm.HMAC256(tokenSecret));
    }

    public String generateToken(UserDetails userDetails, Long ttl) {
        Validate.notNull(userDetails, "User details is null when generating JWT token.");
        Instant issuedAt = Instant.now();
        return JWT.create()
                .withIssuer(tokenIssuer)
                .withSubject(userDetails.getUsername())
                .withIssuedAt(issuedAt)
                .withExpiresAt(issuedAt.plusMillis(ttl))
                .sign(Algorithm.HMAC256(tokenSecret));
    }

    /**
     * Validates the JWT token in String format and returns the subject from it.
     *
     * @param token                         JWT token as String.
     * @return                              The subject of the token.
     */
    public String validateJwtToken(String token) {
        Validate.notBlank(token, "Token is null or blank when validating.");
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(tokenSecret))
                    .withIssuer(tokenIssuer)
                    .build()
                    .verify(token);
            String subject = decodedJWT.getSubject();
            Validate.notBlank(subject);
            return subject;
        } catch (Exception e) {
            return null;
        }
    }
}
