package com.cosmo.cats.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtTestUtil {
    private final KeyPair keyPair;

    public JwtTestUtil() {
        this.keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);
    }

    public String createJwtToken(String username, List<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(username)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(1, ChronoUnit.HOURS)))
                .claim("preferred_username", username)
                .claim("resource_access", Map.of(
                        "cosmo-cats-api", Map.of("roles", roles)
                ))
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .compact();
    }

    public String createAdminToken() {
        return createJwtToken("admin", List.of("ADMIN"));
    }

    public String createSellerToken() {
        return createJwtToken("seller", List.of("SELLER"));
    }

    public String createUserToken() {
        return createJwtToken("user", List.of("USER"));
    }
}
