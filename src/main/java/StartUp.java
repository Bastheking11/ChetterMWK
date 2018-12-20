import domain.entity.Linked.Member;
import domain.entity.Party;
import domain.entity.User;
import service.PartyService;
import service.UserService;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Startup
@Singleton
public class StartUp {

    @Inject
    private UserService us;

    @Inject
    private PartyService ps;

    @PostConstruct
    public void initData() {
        us.add(new User("admin@chetter.localhost", "admin", "admin", true));
    }
}
