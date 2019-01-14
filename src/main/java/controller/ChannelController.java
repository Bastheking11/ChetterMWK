package controller;

import domain.entity.Channel;
import domain.entity.Message;
import domain.entity.Party;
import domain.entity.enums.Permission;
import domain.utility.authentication.Authorize;
import domain.viewmodel.ChannelMessageView;
import domain.viewmodel.ChannelView;
import domain.viewmodel.MessageView;
import service.ChannelService;
import service.IService;
import service.PartyService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Stateless
@Path(APIController.api_path + "/party/{pid}/channel")
public class ChannelController extends APIController<Channel> {

    @Inject
    PartyService ps;

    @Inject
    ChannelService cs;

    @Override
    protected IService getService() {
        return ps;
    }

    @GET
    @Authorize(hasPermissions = {Permission.READ, Permission.UPDATE})
    public Response Get(@PathParam("pid") long pid) {
        Party party = ps.get(pid);
        return response(party.getChannels(), ChannelView.class);
    }

    @GET
    @Path("{cid}")
    @Authorize
    public Response Get(@PathParam("pid") long pid, @PathParam("cid") long cid) {
        Channel channel = cs.get(cid);

        if (channel.getParty().getId() != pid)
            return notFound();
        return response(channel, ChannelView.class);
    }

    @PUT
    @Authorize(hasPermissions = {Permission.UPDATE})
    public Response Create(@PathParam("pid") long pid, Channel entity) {
        Party p = ps.get(pid);
        p.addChannel(entity);
        return response(entity, ChannelView.class);
    }

    @DELETE
    @Authorize(hasPermissions = Permission.DELETE)
    public Response Delete(@PathParam("pid") long pid, Channel entity) {
        if ((cs.get(entity.getId())).getId() != pid)
            return notFound();

        cs.delete(entity);
        return success();
    }

    @PATCH
    @Authorize(hasPermissions = Permission.UPDATE)
    public Response Update(@PathParam("pid") long pid, Channel entity) {
        if ((cs.get(entity.getId())).getParty().getId() != pid)
            return notFound();

        cs.update(entity);
        return success();
    }

    @PUT
    @Path("{cid}/message")
    @Authorize(hasPermissions = Permission.WRITE)
    public Response Send(@PathParam("pid") long pid, @PathParam("cid") long cid, Message entity) {
        Channel channel = cs.get(cid);

        if (entity.getCreator() == null)
            entity.setCreator(getAuthenticated());

        if (channel.getParty().getId() != pid)
            return notFound();

        entity.setChannel(channel);

        MessageSocketController.messageToChannel(entity);
        return success();
    }

    @DELETE
    @Path("{cid}/message")
    @Authorize(hasPermissions = Permission.REMOVE)
    public Response Remove(@PathParam("pid") long pid, @PathParam("cid") long cid, Message entity) {
        if (cs.get(cid).getParty().getId() != pid || entity.getChannel().getId() != cid)
            return notFound();

        cs.deleteMessage(entity);
        return success();
    }

    @GET
    @Path("{cid}/message")
    @Authorize(hasPermissions = Permission.READ)
    public Response Messages(
            @PathParam("pid") long pid,
            @PathParam("cid") long cid,
            @QueryParam("start") @DefaultValue("0") int start,
            @QueryParam("length") @DefaultValue("50") int length
    ) {
        Channel channel = cs.get(cid);
        if (channel.getParty().getId() != pid)
            return notFound();

        return response(cs.getMessages(channel, start, start + length), MessageView.class);
    }
}
