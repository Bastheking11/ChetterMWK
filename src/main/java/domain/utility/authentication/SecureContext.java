package domain.utility.authentication;

import domain.entity.User;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class SecureContext implements SecurityContext {
    public static final String ADMIN = "administrator";

    private final User user;

    public SecureContext(User user) {
        this.user = user;
    }

    @Override
    public Principal getUserPrincipal() {
        return this.user;
    }

    @Override
    public boolean isUserInRole(String s) {
        return s.equals(ADMIN) && this.user.isAdministrator();
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public String getAuthenticationScheme() {
        return SecureContext.BASIC_AUTH;
    }

    public static void setSecurityContext(ContainerRequestContext ctx, User user) {
        ctx.setSecurityContext(new SecureContext(user));
    }
}
