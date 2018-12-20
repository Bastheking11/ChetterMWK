package domain.utility.authentication;

import domain.entity.enums.Permission;

import javax.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@NameBinding
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface Authorize {

    boolean isSignedIn() default true;

    boolean isAdmin() default false;

    Permission[] hasPermissions() default {Permission.READ};

    boolean inParty() default true;
}
