package com.cosmo.cats.service;

import com.cosmo.cats.persistence.entity.ApiKeyEntity;
import com.cosmo.cats.persistence.repository.ApiKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ApiKeyService {
    private final ApiKeyRepository apiKeyRepository;

    @Transactional
    public ApiKeyEntity createApiKey(String name, String description, Set<String> roles, LocalDateTime expiresAt) {
        ApiKeyEntity apiKey = new ApiKeyEntity();
        apiKey.setName(name);
        apiKey.setDescription(description);
        apiKey.setRoles(roles);
        apiKey.setExpiresAt(expiresAt);
        apiKey.setApiKey(generateApiKey());
        
        return apiKeyRepository.save(apiKey);
    }

    @Transactional
    public void deactivateApiKey(String apiKey) {
        apiKeyRepository.findByApiKey(apiKey).ifPresent(entity -> {
            entity.setActive(false);
            apiKeyRepository.save(entity);
        });
    }

    private String generateApiKey() {
        return KeyGenerators.string().generateKey();
    }
}
