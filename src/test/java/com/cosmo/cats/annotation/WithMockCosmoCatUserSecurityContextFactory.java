package com.cosmo.cats.annotation;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class WithMockCosmoCatUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCosmoCatUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCosmoCatUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "RS256");

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", annotation.username());
        claims.put("preferred_username", annotation.username());
        claims.put("scope", "openid profile email");
        
        Map<String, Object> resourceAccess = new HashMap<>();
        Map<String, Object> cosmoCatsApi = new HashMap<>();
        cosmoCatsApi.put("roles", Arrays.asList(annotation.roles()));
        resourceAccess.put("cosmo-cats-api", cosmoCatsApi);
        claims.put("resource_access", resourceAccess);

        Jwt jwt = new Jwt(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(300),
            headers,
            claims
        );

        Authentication auth = new JwtAuthenticationToken(
            jwt,
            Arrays.stream(annotation.roles())
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList())
        );

        context.setAuthentication(auth);
        return context;
    }
}
