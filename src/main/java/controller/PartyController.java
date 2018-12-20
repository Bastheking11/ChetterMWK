package controller;

import domain.entity.Party;
import domain.entity.User;
import domain.entity.enums.Permission;
import domain.utility.authentication.Authorize;
import domain.viewmodel.PartyView;
import service.IService;
import service.PartyService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Stateless
@Path(APIController.api_path + "/party")
public class PartyController extends APIControllerCRUD<Party> {

    @Inject
    PartyService ps;

    @Override
    protected PartyService getService() {
        return ps;
    }

    @Override
    @GET
    @Authorize(isAdmin = true)
    public Response Get() {
        return response(ps.get(), PartyView.class);
    }

    @Override
    @GET
    @Path("{pid}")
    @Authorize
    public Response Get(@PathParam("pid") long id) {
        return response(ps.get(id), PartyView.class);
    }

    @Override
    @PUT
    @Authorize
    public Response Create(Party entity) {
        return response(ps.add(entity), PartyView.class);
    }

    @Override
    @PATCH
    @Authorize(hasPermissions = Permission.UPDATE)
    public Response Update(Party entity) {
        return response(ps.update(entity), PartyView.class);
    }

    @GET
    @Path("search")
    @Authorize
    public Response Search(@DefaultValue("") @QueryParam("query") String query) {
        if (query.isEmpty())
            return response();
        return response(ps.search(query), PartyView.class);
    }

    @DELETE
    @Path("{pid}/unsubscribe")
    @Authorize
    public Response Unsubscribe(@PathParam("pid") long pid) {
        User u = getAuthenticated();
        Party p = ps.get(pid);
        ps.unsubscribe(p, u);
        return success();
    }

    @PUT
    @Path("{pid}/subscribe")
    @Authorize(inParty = false)
    public Response Subscribe(@PathParam("pid") long pid) {
        User u = getAuthenticated();
        Party p = ps.get(pid);
        ps.subscribe(p, u);
        return success();
    }

}
