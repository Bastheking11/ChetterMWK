package manager;

import domain.entity.Channel;
import domain.entity.Message;

import javax.persistence.TypedQuery;
import java.util.Comparator;
import java.util.stream.Stream;

public class MessageManager extends DataManager<Message> {
    public MessageManager() {
        super(Message.class);
    }

    @Override
    public Stream<Message> all() {
        TypedQuery<Message> query = getEntityManager().createQuery("SELECT m FROM Message m ORDER BY m.created DESC", Message.class);
        return query.getResultStream();
    }

    public Stream<Message> all(Channel channel) {
        TypedQuery<Message> query = getEntityManager().createQuery("SELECT m FROM Message m WHERE m.channel = :channel ORDER BY m.created DESC", Message.class);
        query.setParameter("channel", channel);
        return query.getResultStream();
    }
}
