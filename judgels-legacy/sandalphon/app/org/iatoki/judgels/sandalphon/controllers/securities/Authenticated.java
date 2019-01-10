package org.iatoki.judgels.sandalphon.controllers.securities;

import play.mvc.Security;
import play.mvc.With;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@With(AuthenticatedAction.class)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Authenticated {
    Class<? extends Security.Authenticator>[] value() default Security.Authenticator.class;
}
