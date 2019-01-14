package domain.viewmodel;

import domain.entity.Linked.Member;
import domain.entity.Party;
import domain.entity.Role;
import domain.utility.Converter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PartyView extends ViewModel<Party> {

    private String name;
    private String description;
    private String image;

    private ProfileView owner;
    private Set<ChannelView> channels;

    @Converter(ignore = true)
    private Set<ProfileView> subscribers;
    private Set<RoleView> roles;

    @Override
    protected void setup(Party root) {
        super.setup(root);

        subscribers = Convert(
                root.getSubscribers().stream().map(Member::getUser).collect(Collectors.toSet()),
                ProfileView.class,
                depth - 1
        );
    }

    public PartyView setName(String name) {
        this.name = name;
        return this;
    }

    public PartyView setDescription(String description) {
        this.description = description;
        return this;
    }

    public PartyView setImage(String image) {
        this.image = image;
        return this;
    }

    public PartyView setOwner(ProfileView owner) {
        this.owner = owner;
        return this;
    }

    public PartyView setChannels(Set<ChannelView> channels) {
        this.channels = channels;
        return this;
    }

    public PartyView setSubscribers(Set<ProfileView> subscribers) {
        this.subscribers = subscribers;
        return this;
    }

    public PartyView setRoles(Set<RoleView> roles) {
        this.roles = roles;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public ProfileView getOwner() {
        return owner;
    }

    public Set<ChannelView> getChannels() {
        return channels;
    }

    public Set<ProfileView> getSubscribers() {
        return subscribers;
    }

    public Set<RoleView> getRoles() {
        return roles;
    }
}
