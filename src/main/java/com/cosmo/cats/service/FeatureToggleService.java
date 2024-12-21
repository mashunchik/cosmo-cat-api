package com.cosmo.cats.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FeatureToggleService {

    @Value("${features.enabled.new-pricing-algorithm:false}")
    private boolean newPricingAlgorithmEnabled;

    @Value("${features.enabled.enhanced-search:false}")
    private boolean enhancedSearchEnabled;

    @Value("${features.enabled.premium-features:false}")
    private boolean premiumFeaturesEnabled;

    @Value("${feature.cosmoCats.enabled:false}")
    private boolean cosmoCatsEnabled;

    @Value("${feature.kittyProducts.enabled:false}")
    private boolean kittyProductsEnabled;

    public boolean isNewPricingAlgorithmEnabled() {
        return newPricingAlgorithmEnabled;
    }

    public boolean isEnhancedSearchEnabled() {
        return enhancedSearchEnabled;
    }

    public boolean isPremiumFeaturesEnabled() {
        return premiumFeaturesEnabled;
    }

    public boolean isCosmoCatsEnabled() {
        return cosmoCatsEnabled;
    }

    public boolean isKittyProductsEnabled() {
        return kittyProductsEnabled;
    }
}
