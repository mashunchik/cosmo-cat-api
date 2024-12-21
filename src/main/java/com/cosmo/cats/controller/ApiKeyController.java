package com.cosmo.cats.controller;

import com.cosmo.cats.persistence.entity.ApiKeyEntity;
import com.cosmo.cats.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {
    private final ApiKeyService apiKeyService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiKeyEntity> createApiKey(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam Set<String> roles,
            @RequestParam(required = false) LocalDateTime expiresAt) {
        
        ApiKeyEntity apiKey = apiKeyService.createApiKey(name, description, roles, expiresAt);
        return ResponseEntity.ok(apiKey);
    }

    @PostMapping("/{apiKey}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateApiKey(@PathVariable String apiKey) {
        apiKeyService.deactivateApiKey(apiKey);
        return ResponseEntity.ok().build();
    }
}
