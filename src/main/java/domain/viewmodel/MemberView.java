package domain.viewmodel;

import domain.entity.Linked.Member;

import java.util.Set;
import java.util.stream.Collectors;

public class MemberView extends ViewModel<Member> {

    private PartyView party;
    private ProfileView profile;

    private Set<String> permissions;
    private Set<RoleView> roles;

    @Override
    protected void setup(Member root) {
        super.setup(root);

        permissions = root.getParty().getPermissions().stream().map(Enum::name).distinct().collect(Collectors.toSet());
        root.getRoles().forEach(
                role -> permissions.addAll(role.getPermissions().stream().map(Enum::name).collect(Collectors.toSet()))
        );

        if (!root.getUsername().isEmpty())
            profile.setUsername(root.getUsername());
    }

    public MemberView setParty(PartyView party) {
        this.party = party;
        return this;
    }

    public MemberView setProfile(ProfileView profile) {
        this.profile = profile;
        return this;
    }

    public MemberView setPermissions(Set<String> permissions) {
        this.permissions = permissions;
        return this;
    }

    public MemberView setRoles(Set<RoleView> roles) {
        this.roles = roles;
        return this;
    }

    public PartyView getParty() {
        return party;
    }

    public ProfileView getProfile() {
        return profile;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public Set<RoleView> getRoles() {
        return roles;
    }
}
