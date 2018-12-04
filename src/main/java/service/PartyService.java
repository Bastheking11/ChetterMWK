package service;

import domain.entity.Linked.Member;
import domain.entity.Party;
import domain.entity.User;
import manager.PartyManager;
import manager.UserManager;

import javax.inject.Inject;
import java.util.Set;
import java.util.stream.Collectors;

public class PartyService implements IService<Party> {

    @Inject
    PartyManager pm;

    @Inject
    UserManager um;

    @Override
    public Party get(long id) {
        return pm.get(id);
    }

    @Override
    public Set<Party> get() {
        return pm.all().collect(Collectors.toSet());
    }

    @Override
    public Set<Party> page(int start, int end) {
        return pm.all().skip(start).limit(end - start).collect(Collectors.toSet());
    }

    @Override
    public Party update(Party entity) {
        pm.update(entity);
        return entity;
    }

    @Override
    public Party add(Party entity) {
        return pm.create(entity);
    }

    @Override
    public void delete(Party entity) {
        pm.delete(entity.getId());
    }

    public Set<Party> search(String key) {
        return pm.find(key).collect(Collectors.toSet());
    }

    public Set<Member> getMembers(long id) {
        return get(id).getSubscribers();
    }

    public Member getMember(long pid, long uid) {
        return um.getMember(um.get(uid), pm.get(pid));
    }

    public Member subscribe(Party party, User user) {
        return um.subscribe(user, party);
    }

    public void unsubscribe(Party party, User user) {
        um.unsubscribe(user, party);
    }

}
