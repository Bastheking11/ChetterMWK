package domain.viewmodel;

import java.util.Set;

public class OwnProfileView extends ProfileView {

    private String email;

    private Set<PartyView> owned;

    public OwnProfileView setEmail(String email) {
        this.email = email;
        return this;
    }

    public OwnProfileView setOwned(Set<PartyView> owned) {
        this.owned = owned;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Set<PartyView> getOwned() {
        return owned;
    }
}
