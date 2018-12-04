package domain.viewmodel;

import domain.entity.Linked.Member;
import domain.entity.Party;
import domain.entity.User;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ProfileView extends ViewModel<User> {

    private String username;

    private boolean administrator;
    private Set<PartyView> parties;

    @Override
    protected void setup(User root) {
        super.setup(root);

        Set<Party> _parties = new HashSet<>(root.getOwned());
        _parties.addAll(root.getSubscriptions().stream().map(Member::getParty).collect(Collectors.toSet()));
        parties = Convert(_parties, PartyView.class, depth);
    }

    public ProfileView setUsername(String username) {
        this.username = username;
        return this;
    }

    public ProfileView setAdministrator(boolean administrator) {
        this.administrator = administrator;
        return this;
    }

    public ProfileView setParties(Set<PartyView> parties) {
        this.parties = parties;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAdministrator() {
        return administrator;
    }

    public Set<PartyView> getParties() {
        return parties;
    }
}
