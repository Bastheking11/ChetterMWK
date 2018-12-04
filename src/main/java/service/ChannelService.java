package service;

import domain.entity.Channel;
import domain.entity.Message;
import manager.ChannelManager;
import manager.MessageManager;

import javax.inject.Inject;
import java.util.Set;
import java.util.stream.Collectors;

public class ChannelService implements IService<Channel> {
    @Inject
    ChannelManager cm;

    @Inject
    MessageManager mm;

    @Override
    public Channel get(long id) {
        return cm.get(id);
    }

    @Override
    public Set<Channel> get() {
        return cm.all().collect(Collectors.toSet());
    }

    @Override
    public Set<Channel> page(int start, int end) {
        return cm.all().skip(start).limit(end - start).collect(Collectors.toSet());
    }

    @Override
    public Channel update(Channel entity) {
        cm.update(entity);
        return entity;
    }

    @Override
    public Channel add(Channel entity) {
        return cm.create(entity);
    }

    @Override
    public void delete(Channel entity) {
        cm.delete(entity.getId());
    }

    public Set<Message> getMessages() {
        return mm.all().collect(Collectors.toSet());
    }

    public Set<Message> getMessages(Channel channel) {
        return mm.all(channel).collect(Collectors.toSet());
    }

    public Set<Message> getMessages(Channel channel, int start, int end) {
        return mm.all(channel).skip(start).limit(end - start).collect(Collectors.toSet());
    }

    public Message getMessage(long id) {
        return mm.get(id);
    }


    public void deleteMessage(Message entity) {
        mm.delete(entity.getId());
    }

    public Message createMessage(Message entity) {
        return mm.create(entity);
    }
}
