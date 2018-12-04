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
        User test = new User("Martijnvriens94@live.nl", "pass", "MJJH", true);
        us.add(test);
        Party party = new Party(test, "Test", "Welkom bij de eerste party!", "/test.gif");
        ps.add(party);


    }
}
