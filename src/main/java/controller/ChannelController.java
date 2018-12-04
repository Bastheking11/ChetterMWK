package controller;

import domain.entity.Channel;
import domain.entity.Message;
import domain.entity.Party;
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

// todo :X
@Stateless
@Path(APIController.api_path + "/party/{pid}")
public class ChannelController extends APIController<Channel> {

    @Inject
    PartyService ps;

    @Inject
    ChannelService cs;

    @Override
    protected IService getService() {
        return ps;
    }

    @Override
    public Response Get() {
        return notFound();
    }

    @Override
    public Response Create(Channel entity) {
        return notFound();
    }

    @Override
    public Response Update(Channel entity) {
        return notFound();
    }

    @Override
    public Response Delete(Channel entity) {
        return notFound();
    }

    @Override
    public Response Get(@PathParam("pid") long pid) {
        Party party = ps.get(pid);
        return response(party.getChannels(), ChannelView.class);
    }

    @GET
    @Path("{cid}")
    public Response Get(@PathParam("pid") long pid, @PathParam("cid") long cid) {
        Channel channel = cs.get(cid);

        if (channel.getParty().getId() != pid)
            return notFound();
        return response(channel, ChannelMessageView.class);
    }

    // TOdO: Role checks
    @PUT
    @Path("channel")
    public Response Create(@PathParam("pid") long pid, Channel entity) {
        entity.setParty(ps.get(pid));
        cs.add(entity);
        return response(entity, ChannelView.class);
    }

    @DELETE
    @Path("channel")
    public Response Delete(@PathParam("pid") long pid, Channel entity) {
        if ((cs.get(entity.getId())).getId() != pid)
            return notFound();

        cs.delete(entity);
        return success();
    }

    @PATCH
    @Path("channel")
    public Response Update(@PathParam("pid") long pid, Channel entity) {
        if ((cs.get(entity.getId())).getParty().getId() != pid)
            return notFound();

        cs.update(entity);
        return success();
    }

    @PUT
    @Path("channel/{cid}/message")
    public Response Send(@PathParam("pid") long pid, @PathParam("cid") long cid, Message entity) {
        Channel channel = cs.get(cid);
        if (channel.getParty().getId() != pid)
            return notFound();

        entity.setChannel(channel);
        cs.createMessage(entity);

        return success();
    }

    @DELETE
    @Path("channel/{cid}/message")
    public Response Remove(@PathParam("pid") long pid, @PathParam("cid") long cid, Message entity) {
        if (cs.get(cid).getParty().getId() != pid || entity.getChannel().getId() != cid)
            return notFound();

        cs.deleteMessage(entity);
        return success();
    }

    @GET
    @Path("channel/{cid}/message")
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
