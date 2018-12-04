package domain.entity;

import domain.entity.enums.Permission;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 50)
    @Pattern(regexp = "[A-Za-z0-9_-]{3,50}")
    private String name;

    @Column(length = 500)
    private String description;

    private String image;

    @ManyToOne
    private Party party;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Permission.class)
    private Set<Permission> permissions;
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "channel", orphanRemoval = true)
    private Set<Message> messages;

    public Channel(Party party, String name, String description, String image, Set<Permission> permissions) {
        setName(name);
        setDescription(description);
        setImage(image);
        setParty(party);
        setPermissions(permissions);
        setMessages(new HashSet<>());
    }

    public Channel() {
    }

    public Party getParty() {
        return party;
    }

    public Channel setParty(Party party) {
        this.party = party;
        party.addChannel(this);

        return this;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Channel setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Channel setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getImage() {
        return image;
    }

    public Channel setImage(String image) {
        this.image = image;
        return this;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Channel setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
        return this;
    }

    public Channel addPermissions(Set<Permission> permissions) {
        this.permissions.addAll(permissions);
        return this;
    }

    public Channel addPermission(Permission permission) {
        this.permissions.add(permission);
        return this;
    }

    public Channel removePermissions(Set<Permission> permissions) {
        this.permissions.removeAll(permissions);
        return this;
    }

    public Channel removePermission(Permission permission) {
        this.permissions.remove(permission);
        return this;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public Channel setMessages(Set<Message> messages) {
        this.messages = messages;
        return this;
    }

    public Channel addMessage(Message message) {
        if (this.messages.add(message) && message.getChannel() != this)
            message.setChannel(this);

        return this;
    }
}
