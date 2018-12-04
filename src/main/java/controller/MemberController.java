package controller;

import domain.entity.Linked.Member;
import domain.entity.Party;
import domain.entity.User;
import domain.viewmodel.MemberView;
import service.IService;
import service.PartyService;
import service.UserService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Stateless
@Path(APIController.api_path + "/party/{pid}/user")
public class MemberController extends APIController<Member> {

    @Inject
    PartyService ps;

    @Inject
    UserService us;

    @Override
    protected IService getService() {
        return ps;
    }

    @Override
    public Response Get() {
        return notFound();
    }

    @Override
    public Response Create(Member entity) {
        return notFound();
    }

    @Override
    public Response Update(Member entity) {
        return notFound();
    }

    @Override
    public Response Get(@PathParam("pid") long pid) {
        Party party = ps.get(pid);
        return response(party.getSubscribers(), MemberView.class);
    }

    @GET
    @Path("{uid}")
    public Response Get(@PathParam("pid") long pid, @PathParam("uid") long uid) {
        return response(ps.getMember(pid, uid), MemberView.class);
    }

    @PUT
    @Path("{uid}/subscribe")
    public Response Subscribe(@PathParam("pid") long pid, @PathParam("uid") long uid) {
        User u = us.get(uid);
        Party p = ps.get(pid);
        ps.subscribe(p, u);
        return success();
    }

    @DELETE
    @Path("{uid}/unsubscribe")
    public Response Unsubscribe(@PathParam("pid") long pid, @PathParam("uid") long uid) {
        User u = us.get(uid);
        Party p = ps.get(pid);
        ps.unsubscribe(p, u);
        return success();
    }
}
