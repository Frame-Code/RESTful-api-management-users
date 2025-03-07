package com.firstSpring.firstSpring.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.firstSpring.firstSpring.model.UserEntity;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtUtils {
    private static final Logger LOG = Logger.getLogger(JwtUtils.class.getName());
    @Value("${security.jwt.secret-key}")
    private String privateKey;

    @Value("${security.jwt.user.generator}")
    private String userGenerator;

    @Value("${security.jwt.expiration}")
    private long expirationToken;

    @Value("${security.jwt.refresh-token}")
    private long expirationRefreshToken;

    public Optional<String> createToken(@NotNull Authentication authentication) {
        Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

        String username = authentication.getPrincipal().toString();
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(",")); //Delimiting each authority with a comma
        return buildToken(username, authorities, algorithm, this.expirationToken);
    }

    public Optional<String> createRefreshToken(Authentication authentication) {
        Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

        String username = authentication.getPrincipal().toString();
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return buildToken(username, authorities, algorithm, this.expirationRefreshToken);
    }

    private Optional<String> buildToken(String username, String authorities, Algorithm algorithm, long expiration) {
        try {
            return Optional.of(JWT.create()
                    .withIssuer(this.userGenerator)
                    .withSubject(username)
                    .withClaim("authorities", authorities)
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                    .withJWTId(UUID.randomUUID().toString())
                    .withNotBefore(new Date(System.currentTimeMillis()))
                    .sign(algorithm));
        } catch (JWTCreationException exception) {
            LOG.log(Level.SEVERE, "Error creating the token: INVALID CONFIGURATION OR COULDN'T CONVERT CLAIMS");
            return Optional.empty();
        }
    }

    public DecodedJWT validateToken(String token) throws JWTVerificationException{
        Algorithm algorithm = Algorithm.HMAC256(this.privateKey);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(this.userGenerator)
                .build();
        return verifier.verify(token); //If the token is valid, that return an object DecodedJWT
    }

    public String extracUsername(DecodedJWT decodedJWT) {
        return decodedJWT.getSubject();
    }

    public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName) {
        return decodedJWT.getClaim(claimName);
    }

    public Map<String, Claim> getAllClaims(DecodedJWT decodedJWT) {
        return decodedJWT.getClaims();
    }

    public boolean isTokenValid(final DecodedJWT decodedJWT, final UserEntity user) {
        final String email = extracUsername(decodedJWT);
        return user.getEmail().equals(email) && !isTokenExpired(decodedJWT);
    }

    public boolean isTokenExpired(DecodedJWT decodedJWT) {
        return decodedJWT.getExpiresAt().before(new Date());
    }
}
