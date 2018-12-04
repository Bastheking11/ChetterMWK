package service_test;

import domain.entity.User;
import manager.UserManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import service.UserService;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.core.Context;

public class UserServiceTest {

    @Inject
    UserService users;

    @Before
    void setup(@Context EntityManager em) {
        // Clean
        Query cleanUsers = em.createQuery("DELETE FROM User");
        cleanUsers.executeUpdate();
    }

    @Test
    void add() {
        int create = 5;

        for (int i = 0; i < create; i++) {
            users.add(new User(
                    "testuser__" + i + "@chetter.root",
                    "password",
                    "testuser__" + i
            ));
        }

        Assert.assertEquals("All users created", create, users.get().size());

    }

}
