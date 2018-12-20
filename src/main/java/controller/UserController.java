package controller;

import domain.entity.User;
import domain.utility.authentication.Authorize;
import domain.viewmodel.MemberView;
import domain.viewmodel.OwnProfileView;
import domain.viewmodel.ProfileView;
import domain.viewmodel.UserView;
import service.UserService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Stateless
@Path(APIController.api_path + "/user")
public class UserController extends APIControllerCRUD<User> {

    @Inject
    UserService us;

    @Override
    protected UserService getService() {
        return us;
    }

    @Override
    @GET
    @Authorize(isAdmin = true)
    public Response Get() {
        return response(getService().get(), UserView.class);
    }

    @Override
    @PUT
    @Authorize
    public Response Create(User user) {
        return response(getService().add(user), UserView.class);
    }

    @Override
    @PATCH
    @Authorize
    public Response Update(User user) {
        return response(getService().update(user), UserView.class);
    }

    @Override
    @GET
    @Path("{uid}")
    @Authorize(isAdmin = true)
    public Response Get(@PathParam("uid") long uid) {
        return response(getService().get(uid), UserView.class);
    }

    @GET
    @Path("self")
    @Authorize
    public Response GetSelf() {
        User self = getAuthenticated();
        return Get(self.getId());
    }

    @GET
    @Path("{uid}/parties")
    @Authorize(isAdmin = true)
    public Response GetParties(@PathParam("uid") long uid) {
        return response(getService().get(uid).getSubscriptions(), MemberView.class);
    }

    @GET
    @Path("self/parties")
    @Authorize
    public Response GetSelfParties() {
        User self = getAuthenticated();
        return GetParties(self.getId());
    }

    @GET
    @Path("{uid}/profile")
    @Authorize
    public Response GetProfile(@PathParam("uid") long uid) {
        try {
            User self = getAuthenticated();
            if (self.getId() == uid)
                return GetSelfProfile();

            if (self.isAdministrator())
                return response(getService().get(uid), OwnProfileView.class);
        } catch (NotAuthorizedException ignore) {
        }

        return response(getService().get(uid), ProfileView.class);

    }

    @GET
    @Path("self/profile")
    @Authorize
    public Response GetSelfProfile() {
        User self = getAuthenticated();
        return response(getService().get(self.getId()), OwnProfileView.class);
    }
}
