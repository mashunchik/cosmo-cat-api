package com.cosmo.cats.aspect;

import com.cosmo.cats.annotation.FeatureToggle;
import com.cosmo.cats.exception.FeatureNotAvailableException;
import com.cosmo.cats.service.FeatureToggleService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class FeatureToggleAspect {

    private final FeatureToggleService featureToggleService;

    public FeatureToggleAspect(FeatureToggleService featureToggleService) {
        this.featureToggleService = featureToggleService;
    }

    @Before("@annotation(com.cosmo.cats.annotation.FeatureToggle)")
    public void checkFeatureEnabled(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        FeatureToggle featureToggle = method.getAnnotation(FeatureToggle.class);
        
        String featureName = featureToggle.feature();
        boolean isEnabled = switch (featureName) {
            case "cosmoCats" -> featureToggleService.isCosmoCatsEnabled();
            case "kittyProducts" -> featureToggleService.isKittyProductsEnabled();
            default -> throw new IllegalArgumentException("Unknown feature: " + featureName);
        };

        if (!isEnabled) {
            throw new FeatureNotAvailableException("Feature '" + featureName + "' is not available");
        }
    }
}
