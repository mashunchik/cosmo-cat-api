package com.cosmo.cats.security.apikey;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {
    private final String apiKey;
    private final String name;

    public ApiKeyAuthenticationToken(String apiKey, String name, Set<String> roles) {
        super(roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet()));
        this.apiKey = apiKey;
        this.name = name;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return apiKey;
    }

    @Override
    public Object getPrincipal() {
        return name;
    }
}
