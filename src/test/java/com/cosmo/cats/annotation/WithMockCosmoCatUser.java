package com.cosmo.cats.annotation;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCosmoCatUserSecurityContextFactory.class)
public @interface WithMockCosmoCatUser {
    String username() default "test-user";
    String[] roles() default {"USER"};
}
