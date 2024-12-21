package com.cosmo.cats.service;

import com.cosmo.cats.annotation.FeatureToggle;
import com.cosmo.cats.model.CosmoCat;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CosmoCatService {

    @FeatureToggle(feature = "cosmoCats")
    public List<CosmoCat> getCosmoCats() {
        // Sample data - in a real application, this would come from a database
        return List.of(
            CosmoCat.builder()
                .id(UUID.randomUUID().toString())
                .name("Stellar")
                .spacesuit("Silver Nebula Suit")
                .planet("Mars")
                .build(),
            CosmoCat.builder()
                .id(UUID.randomUUID().toString())
                .name("Nova")
                .spacesuit("Golden Star Suit")
                .planet("Venus")
                .build()
        );
    }
}
