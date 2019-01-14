package domain.utility.authentication;

import domain.entity.User;
import service.UserService;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;

import static javax.ws.rs.Priorities.AUTHENTICATION;

@Authorize
@Provider
@Priority(AUTHENTICATION)
// https://stackoverflow.com/a/26778123/5158376
public class AuthenticationFilter extends Auth implements ContainerRequestFilter {

    @Inject
    UserService us;

    @Context
    private ResourceInfo resource;

    private String getAccessToken(ContainerRequestContext ctx) {
        String authHeader = ctx.getHeaderString(HttpHeaders.AUTHORIZATION);
        String accessToken;

        // If no authorization header is sent, check the URI query parameter according to the Bearer spec
        // http://self-issued.info/docs/draft-ietf-oauth-v2-bearer.html#query-param
        if (authHeader == null || authHeader.isEmpty()) {
            // Extract the token from the query parameter
            accessToken = ctx.getUriInfo().getQueryParameters().getFirst("access_token");
        } else {
            // Extract the token from the HTTP Authorization header
            accessToken = authHeader.substring(AUTHENTICATION_SCHEME.length()).trim();
        }

        return accessToken;
    }

    private User getAccessUser(String token) {
//        Jws<Claims> claims = Jwts.parser().setSigningKey(KeyGenerator.generate(AuthenticationController.SECRET)).parseClaimsJws(token);
//        String subject = claims.getBody().getSubject();
        String subject = token;
        if (subject.matches("user/(\\d+)")) {
            long id = Long.parseLong(subject.substring(5));
            return us.get(id);
        }

        return null;
    }

    @Override
    public void filter(ContainerRequestContext reqCtx) throws IOException {
        final String token = getAccessToken(reqCtx);

        Method resourceMethod = resource.getResourceMethod();
        Authorize annotation = resourceMethod.getAnnotation(Authorize.class);

        if (token == null || token.isEmpty()) {
            if (!annotation.isSignedIn())
                return;

            abortWithUnauthorized(reqCtx);
            return;
        }

        User login = getAccessUser(token);

        if (login == null) {
            if (!annotation.isSignedIn())
                return;

            abortWithUnauthorized(reqCtx);
            return;
        }

        final SecurityContext sec = reqCtx.getSecurityContext();
        reqCtx.setSecurityContext(new SecureContext(login) {
            @Override
            public boolean isSecure() {
                return sec.isSecure();
            }

            @Override
            public String getAuthenticationScheme() {
                return sec.getAuthenticationScheme();
            }
        });
    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                .startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

}
