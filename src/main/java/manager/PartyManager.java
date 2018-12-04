package manager;

import domain.entity.Party;

import javax.persistence.TypedQuery;
import java.util.stream.Stream;

public class PartyManager extends DataManager<Party> {

    public PartyManager() {
        super(Party.class);
    }

    @Override
    public Stream<Party> all() {
        TypedQuery<Party> query = getEntityManager().createNamedQuery("party:Get", Party.class);
        return query.getResultStream();
    }

    public Stream<Party> find(String key) {
        TypedQuery<Party> query = getEntityManager().createNamedQuery("party:FindByDescription", Party.class);
        query.setParameter("tags", "%" + key + "%");
        return query.getResultStream();
    }
}
