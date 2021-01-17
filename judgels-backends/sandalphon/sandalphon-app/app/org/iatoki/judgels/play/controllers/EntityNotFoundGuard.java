package org.iatoki.judgels.play.controllers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import play.mvc.Security;
import play.mvc.With;

@With(EntityNotFoundGuardAction.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityNotFoundGuard {
    Class<? extends Security.Authenticator>[] value() default Security.Authenticator.class;
}
