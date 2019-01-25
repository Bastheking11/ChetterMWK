package domain.utility.authentication;

import domain.entity.Channel;
import domain.entity.Linked.Member;
import domain.entity.Party;
import domain.entity.User;
import domain.entity.enums.Permission;
import service.ChannelService;
import service.PartyService;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static javax.ws.rs.Priorities.AUTHORIZATION;

@Authorize
@Provider
@Priority(AUTHORIZATION)
public class AuthorizationFilter extends Auth implements ContainerRequestFilter {

    @Inject
    PartyService ps;

    @Inject
    ChannelService cs;

    @Context
    private ResourceInfo resource;

    @Context
    private HttpServletRequest servlet;

    @Override
    public void filter(ContainerRequestContext reqCtx) throws IOException {
        Class<?> resourceClass = resource.getResourceClass();
        Method resourceMethod = resource.getResourceMethod();

        Authorize annotation = resourceMethod.getAnnotation(Authorize.class);
        final Principal principal = reqCtx.getSecurityContext().getUserPrincipal();

        if (!annotation.isSignedIn() && principal == null) {
            return;
        } else if ((!annotation.isSignedIn() && principal != null) || (annotation.isSignedIn() && principal == null)) {
            abortWithUnauthorized(reqCtx);
            return;
        }

        // Check method annotations for authorization and compare with user
        User user = (User) principal;
        Map<String, Long> pathParams = new HashMap<String, Long>();
        reqCtx.getUriInfo().getPathParameters().forEach((s, strings) -> pathParams.put(s, Long.parseLong(strings.get(0))));

        if (user == null) abortWithUnauthorized(reqCtx);

        // Administrator
        if (user.isAdministrator()) {
            return;
        } else if (annotation.isAdmin()) {
            abortWithUnauthorized(reqCtx);
            return;
        }

        // Permissions
        if (pathParams.containsKey("pid")) {
            Party party = ps.get(pathParams.get("pid"));
            if (party == null) return;

            Member m = ps.getMember(party.getId(), user.getId());

            if ((m == null && annotation.inParty()) || (m != null && !annotation.inParty())) {
                abortWithUnauthorized(reqCtx);
                return;
            } else if (m == null && !annotation.inParty()) {
                return;
            }

            if (m == null) {
                abortWithUnauthorized(reqCtx);
                return;
            }

            if (m.getUser() == party.getOwner())
                return;

            Set<Permission> permissions;
            if (pathParams.containsKey("cid")) {
                Channel c = cs.get(pathParams.get("cid"));
                if (c == null) return;

                permissions = m.getPermissions(c);
            } else {
                permissions = m.getPermissions();
            }

            if (!permissions.containsAll(Arrays.asList(annotation.hasPermissions()))) {
                abortWithUnauthorized(reqCtx);
                return;
            }
        }
    }
}
