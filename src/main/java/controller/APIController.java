package controller;

import domain.entity.User;
import domain.viewmodel.ViewModel;
import domain.viewmodel.UserView;
import service.IService;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.security.Principal;
import java.util.Set;

@Produces(MediaType.APPLICATION_JSON)
public abstract class APIController<T> {

    static final String version = "v1";
    static final String api_path = "/api/" + APIController.version;

    protected abstract IService getService();

    @Context
    protected SecurityContext ctx;

    // Responses
    public static Response response(Link... links) {
        return Response.ok("{}").links(links).build();
    }

    public static Response response(Object entity, Class<? extends ViewModel> vm, Link... links) {
        if (entity == null)
            return notFound(links);

        if (entity instanceof Set)
            return Response.ok(ViewModel.Convert((Set) entity, vm)).links(links).build();

        return Response.ok(ViewModel.Convert(entity, vm)).links(links).build();
    }

    public static Response response(UserView entity, Link... links) {
        return Response.ok(entity).links(links).build();
    }

    public static Response response(Set<UserView> entities, Link... links) {
        return Response.ok(entities).links(links).build();
    }

    public static Response success(Link... links) {
        return Response.ok().links(links).build();
    }

    public static Response notFound(Link... links) {
        return Response.status(Response.Status.NOT_FOUND).links(links).build();
    }

    public static Response created(Object entity, URI location, Link... links) {
        return Response.created(location).entity(entity).links(links).build();
    }

    public static Response forbidden(Link... links) {
        return Response.status(Response.Status.FORBIDDEN).links(links).build();
    }

    public static Response unauthorized(Link... links) {
        return Response.status(Response.Status.UNAUTHORIZED).links(links).build();
    }

    User getAuthenticated() {
        Principal auth = ctx.getUserPrincipal();
        if (auth == null) {
            throw new NotAuthorizedException("NOT FOUND");
        }

        return (User) auth;
    }

}
