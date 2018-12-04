package service;

import domain.entity.User;
import manager.UserManager;

import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

@Model
public class UserService implements IService<User>, Serializable {

    @Inject
    private UserManager um;

    @Override
    public User get(long id) {
        User result = um.get(id);

        return result;
    }

    @Override
    public Set<User> get() {
        return um.all().collect(Collectors.toSet());
    }

    @Override
    public Set<User> page(int start, int end) {
        return um.all().skip(start).limit(end - start).collect(Collectors.toSet());
    }

    @Override
    public User update(User user) {
        um.update(user);
        return user;
    }

    @Override
    public User add(User user) {
        return um.create(user);
    }

    @Override
    public void delete(User user) {
        um.delete(user.getId());
    }

    public User authenticate(String email, String password) {
        User user = um.getByEmail(email);

        if (um.authenticate(user, password)) {
            return user;
        }
        throw new NotAuthorizedException("AUTH");
    }

}
