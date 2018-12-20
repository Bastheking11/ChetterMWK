package manager;

import domain.entity.Linked.Member;
import domain.entity.Party;
import domain.entity.User;
import domain.viewmodel.ProfileView;
import domain.viewmodel.UserView;
import domain.viewmodel.ViewModel;

import javax.enterprise.inject.Model;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Objects;
import java.util.stream.Stream;

@Model
public class UserManager extends DataManager<User> {

    UserManager() {
        super(User.class);
    }

    @Override
    public User create(User entity) {
        entity.setSalt(User.generateSalt());
        entity.setPassword(User.SHA512(entity.getPassword(), entity.getSalt()));

        return super.create(entity);
    }

    @Override
    public void update(User entity) {
        if (!this.get(entity.getId()).getPassword().equals(entity.getPassword())) {
            entity.setSalt(User.generateSalt());
            entity.setPassword(User.SHA512(entity.getPassword(), entity.getSalt()));
        }

        super.update(entity);
    }

    @Override
    public Stream<User> all() {
        TypedQuery<User> query = getEntityManager().createNamedQuery("user:Get", User.class);
        return query.getResultStream();
    }

    public User searchByUsername(String username) {
        TypedQuery<User> query = getEntityManager().createNamedQuery("user:FindByUsername", User.class);
        query.setParameter("username", "%" + username + "%");

        return query.getSingleResult();
    }

    public User getByEmail(String email) {
        TypedQuery<User> query = getEntityManager().createNamedQuery("user:GetByEmail", User.class);
        query.setParameter("email", email);

        return query.getSingleResult();
    }

    public boolean authenticate(User user, String password) {
        return Objects.equals(user.getPassword(), User.SHA512(password, user.getSalt()));
    }

    public Member getMember(User user, Party party) {
        TypedQuery<Member> query = getEntityManager().createQuery("SELECT m FROM Member m WHERE m.party = :party AND m.user = :user", Member.class);
        query.setParameter("party", party);
        query.setParameter("user", user);

        try {
            return query.getSingleResult();
        } catch (NoResultException ignore) {
        }

        return null;
    }

    public Member subscribe(User user, Party party) {
        getEntityManager().persist(new Member(user, party));
        return getMember(user, party);
    }

    public void unsubscribe(User user, Party party) {
        Member sub = getMember(user, party);
        getEntityManager().remove(sub);
        user.removeSubscription(sub);
        party.removeSubscriber(sub);
    }

}
