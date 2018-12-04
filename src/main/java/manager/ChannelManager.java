package manager;

import domain.entity.Channel;

import javax.persistence.TypedQuery;
import java.util.stream.Stream;

public class ChannelManager extends DataManager<Channel> {
    public ChannelManager() {
        super(Channel.class);
    }

    @Override
    public Stream<Channel> all() {
        TypedQuery<Channel> query = getEntityManager().createQuery("SELECT c FROM Channel c", Channel.class);
        return query.getResultStream();
    }
}
