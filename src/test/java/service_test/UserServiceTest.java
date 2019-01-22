package service_test;

import domain.entity.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import service.UserService;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.NotAuthorizedException;

public class UserServiceTest {

    @Inject
    UserService users;

    String def_pass = "password";

    @PersistenceContext
    EntityManager em;

//    @Before
//    public void setup() {
//        // Clean
//        Query cleanUsers = em.createQuery("DELETE FROM User");
//        cleanUsers.executeUpdate();
//    }

    private User randomUser() {
        String key = RandomStringUtils.random(5, "0123456789ABCDEFGH");
        return new User(
                "testuser_" + key + "@chetter.root",
                def_pass,
                "testuser_" + key
        );
    }

//    @Test
//    public void add() {
//        int create = 5;
//
//        for (int i = 0; i < create; i++) {
//            users.add(randomUser());
//        }
//        Assert.assertEquals("All users created", create, users.get().size());
//
//    }
//
//    @Test
//    public void auth() {
//        User user = users.add(randomUser());
//
//        boolean cought = false;
//        try {
//            users.authenticate(user.getEmail(), def_pass);
//        } catch (NotAuthorizedException e) {
//            cought = true;
//        }
//        Assert.assertFalse("Login failed with right details", cought);
//
//        // ------------------------------------------------------
//
//        boolean cought_2 = false;
//        try {
//            users.authenticate(user.getEmail(), "notMyPass");
//        } catch (NotAuthorizedException e) {
//            cought_2 = true;
//        }
//        Assert.assertTrue("Login succeed with wrong details", cought_2);
//    }
//
//    @Test
//    public void paging() {
//        for (int i = 0; i < 100; i++) {
//            users.add(randomUser());
//        }
//
//        Assert.assertEquals("Page users found", 20, users.page(10, 30).size());
//    }

}
