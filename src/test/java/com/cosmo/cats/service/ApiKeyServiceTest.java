package com.cosmo.cats.service;

import com.cosmo.cats.persistence.entity.ApiKeyEntity;
import com.cosmo.cats.persistence.repository.ApiKeyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiKeyServiceTest {

    @Mock
    private ApiKeyRepository apiKeyRepository;

    @InjectMocks
    private ApiKeyService apiKeyService;

    @Test
    void shouldCreateApiKey() {
        // given
        String name = "Test Key";
        String description = "For testing";
        Set<String> roles = Set.of("SELLER");
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(1);

        when(apiKeyRepository.save(any(ApiKeyEntity.class))).thenAnswer(i -> i.getArgument(0));

        // when
        ApiKeyEntity result = apiKeyService.createApiKey(name, description, roles, expiresAt);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getDescription()).isEqualTo(description);
        assertThat(result.getRoles()).isEqualTo(roles);
        assertThat(result.getExpiresAt()).isEqualTo(expiresAt);
        assertThat(result.getApiKey()).isNotNull();
        assertThat(result.isActive()).isTrue();

        verify(apiKeyRepository).save(any(ApiKeyEntity.class));
    }

    @Test
    void shouldDeactivateApiKey() {
        // given
        String apiKey = "test-key";
        ApiKeyEntity entity = new ApiKeyEntity();
        entity.setApiKey(apiKey);
        entity.setActive(true);

        when(apiKeyRepository.findByApiKey(apiKey)).thenReturn(Optional.of(entity));
        when(apiKeyRepository.save(any(ApiKeyEntity.class))).thenAnswer(i -> i.getArgument(0));

        // when
        apiKeyService.deactivateApiKey(apiKey);

        // then
        verify(apiKeyRepository).findByApiKey(apiKey);
        verify(apiKeyRepository).save(entity);
        assertThat(entity.isActive()).isFalse();
    }

    @Test
    void whenDeactivatingNonExistentKey_thenNoAction() {
        // given
        String apiKey = "non-existent-key";
        when(apiKeyRepository.findByApiKey(apiKey)).thenReturn(Optional.empty());

        // when
        apiKeyService.deactivateApiKey(apiKey);

        // then
        verify(apiKeyRepository).findByApiKey(apiKey);
        verify(apiKeyRepository, never()).save(any());
    }
}
