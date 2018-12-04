package domain.utility.authentication;

import controller.APIController;
import domain.entity.User;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.security.Principal;

public class AuthenticationHandler implements InvocationHandler {

    @Context
    private SecurityContext ctx;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (method.isAnnotationPresent(Authenticate.class)) {

            Authenticate auth = method.getDeclaredAnnotation(Authenticate.class);
            Principal user = ctx.getUserPrincipal();

            if (auth.isSignedIn() && user == null)
                return APIController.unauthorized();
            if (!auth.isSignedIn() && user != null)
                return APIController.forbidden();
            if (auth.isAdmin() && !((User) user).isAdministrator())
                return APIController.unauthorized();
        }

        return method.invoke(proxy, args);
    }
}
