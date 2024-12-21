package com.cosmo.cats.service;

import com.cosmo.cats.exception.FeatureNotAvailableException;
import com.cosmo.cats.model.CosmoCat;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CosmoCatServiceTest {

    @Nested
    @SpringBootTest
    class WhenFeatureEnabled {
        @Autowired
        private CosmoCatService cosmoCatService;

        @Test
        void getCosmoCats_ShouldReturnCats() {
            // When feature is enabled in application.yml
            List<CosmoCat> result = cosmoCatService.getCosmoCats();

            // Then
            assertNotNull(result);
            assertFalse(result.isEmpty());
            result.forEach(cat -> {
                assertNotNull(cat.getId());
                assertNotNull(cat.getName());
                assertNotNull(cat.getSpacesuit());
                assertNotNull(cat.getPlanet());
            });
        }
    }

    @Nested
    @SpringBootTest
    @ActiveProfiles("test")
    class WhenFeatureDisabled {
        @Autowired
        private CosmoCatService cosmoCatService;

        @Test
        void getCosmoCats_ShouldThrowException() {
            // When & Then
            assertThrows(FeatureNotAvailableException.class, () -> {
                cosmoCatService.getCosmoCats();
            });
        }
    }
}
