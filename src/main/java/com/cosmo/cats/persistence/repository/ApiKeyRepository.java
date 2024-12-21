package com.cosmo.cats.persistence.repository;

import com.cosmo.cats.persistence.entity.ApiKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKeyEntity, Long> {
    Optional<ApiKeyEntity> findByApiKey(String apiKey);
}
