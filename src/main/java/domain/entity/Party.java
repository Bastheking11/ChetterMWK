package domain.entity;

import domain.entity.Linked.Member;
import domain.entity.enums.Permission;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
@NamedQueries({
        @NamedQuery(name = "party:FindByDescription", query = "SELECT p FROM Party p WHERE p.description LIKE :tags"),
        @NamedQuery(name = "party:Get", query = "SELECT p FROM Party p")
})
public class Party {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User owner;

    @Column(nullable = false, length = 50)
    @Pattern(regexp = "[A-Za-z0-9_-]{3,50}")
    private String name;

    @Column(length = 500)
    private String description;

    private String image;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Permission.class)
    private Set<Permission> permissions;
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "party")
    private Set<Channel> channels;
    @OneToMany(mappedBy = "party", cascade = CascadeType.REMOVE)
    private Set<Member> subscribers;
    @OneToMany(mappedBy = "party", cascade = CascadeType.REMOVE)
    private Set<Role> roles;

    public Party(User owner, String name, String description, String image, Set<Permission> permissions, Set<Channel> channels, Set<Member> subscribers, Set<Role> roles) {
        setOwner(owner);
        setName(name);
        setDescription(description);
        setImage(image);
        setPermissions(permissions);
        setChannels(channels);
        setSubscribers(subscribers);
        setRoles(roles);
    }

    public Party(User owner, String name, String description, String image) {
        this(owner, name, description, image, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
    }

    public Party() {
    }

    public long getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    public Party setOwner(User owner) {
        this.owner = owner;
        owner.addOwned(this);
        return this;
    }

    public String getName() {
        return name;
    }

    public Party setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Party setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getImage() {
        return image;
    }

    public Party setImage(String image) {
        this.image = image;
        return this;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Party setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
        return this;
    }

    public Party addPermissions(Set<Permission> permissions) {
        this.permissions.addAll(permissions);
        return this;
    }

    public Party addPermission(Permission permission) {
        this.permissions.add(permission);
        return this;
    }

    public Party removePermissions(Set<Permission> permissions) {
        this.permissions.removeAll(permissions);
        return this;
    }

    public Party removePermission(Permission permission) {
        this.permissions.remove(permission);
        return this;
    }

    public Set<Channel> getChannels() {
        return channels;
    }

    public Party setChannels(Set<Channel> channels) {
        this.channels = channels;
        for (Channel c : channels)
            if (c.getParty() != this) c.setParty(this);
        return this;
    }

    public Party addChannel(Channel channel) {
        if (this.channels.add(channel) && channel.getParty() != this)
            channel.setParty(this);

        return this;
    }

    public Set<Member> getSubscribers() {
        return subscribers;
    }

    public Party setSubscribers(Set<Member> subscribers) {
        this.subscribers = subscribers;
        return this;
    }

    public Party addSubscriber(Member subscriber) {
        this.subscribers.add(subscriber);
        return this;
    }

    public Party removeSubscriber(Member subscriber) {
        this.subscribers.remove(subscriber);
        return this;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public Party setRoles(Set<Role> roles) {
        this.roles = roles;
        for (Role r : roles)
            if (r.getParty() != this) r.setParty(this);
        return this;
    }

    public Party addRole(Role role) {
        if (this.roles.add(role) && role.getParty() != this)
            role.setParty(this);
        return this;
    }
}
