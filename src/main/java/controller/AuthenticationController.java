package controller;

import domain.entity.User;
import domain.utility.authentication.Authorize;
import domain.utility.authentication.Credentials;
import service.IService;
import service.UserService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Stateless
@Path(APIController.api_path + "/auth")
public class AuthenticationController extends APIController<User> {
    @PersistenceContext
    EntityManager em;
    final public static String SECRET = "rtxSzmNK01";

    @Inject
    UserService us;

    @Override
    protected IService getService() {
        return us;
    }

    @POST
    @Authorize(isSignedIn = false)
    public Response authenticateUser(Credentials creds) {
        try {
            User login = us.authenticate(creds.getEmail(), creds.getPassword());

            String token = issueToken(login);

            return Response.ok("{\"token\":\"" + token + "\"}").build();
        } catch (Exception e) {
            return forbidden();
        }
    }

    @PUT
    @Authorize(isSignedIn = false)
    public Response registerUser(User user) {
        us.add(user);
        return success();
    }

    @GET
    @Path("clearCache")
    @Authorize(isAdmin = true)
    public Response refreshAll() {
        em.getEntityManagerFactory().getCache().evictAll();
        return success();
    }

    private String issueToken(User subject) {
        return "user/" + subject.getId();
        /*
        Key key = KeyGenerator.generate(SECRET);
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject("user/" + subject.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(OffsetDateTime.now().plusHours(1).toInstant().toEpochMilli()))
                .signWith(SignatureAlgorithm.HS512, key)
                .compact(); */
    }
}
