package domain.entity.Linked;

import domain.entity.Channel;
import domain.entity.Party;
import domain.entity.Role;
import domain.entity.User;
import domain.entity.enums.Permission;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
public class Member {

    private Date since;
    @ManyToMany
    private Set<Role> roles;

    private String username;

    @Id
    @ManyToOne
    private User user;

    @Id
    @ManyToOne
    private Party party;

    public Member(User user, Party party) {
        this.since = new Date();
        this.roles = new HashSet<>();
        this.user = user;
        this.party = party;

        user.addSubscription(this);
        party.addSubscriber(this);
    }

    public Member() {
    }

    public Set<Permission> getPermissions() {
        Set<Permission> allPermissions = new HashSet<>(party.getPermissions());
        this.roles.forEach(role -> allPermissions.addAll(role.getPermissions()));
        return allPermissions;
    }

    public Set<Permission> getPermissions(Channel c) {
        Set<Permission> all = getPermissions();

        if (c.getParty() != this.party)
            return all;

        all.addAll(c.getPermissions());
        return all;
    }

    public Date getSince() {
        return since;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public Member setRoles(Set<Role> roles) {
        this.roles = roles;
        return this;
    }

    public Member removeRoles(Set<Role> roles) {
        this.roles.removeAll(roles);
        return this;
    }

    public Member removeRole(Role role) {
        this.roles.remove(role);
        return this;
    }

    public Member addRoles(Set<Role> roles) {
        this.roles.addAll(roles);
        return this;
    }

    public Member addRole(Role role) {
        this.roles.add(role);
        return this;
    }

    public User getUser() {
        return user;
    }

    public Party getParty() {
        return party;
    }

    public String getUsername() {
        return username;
    }

    public Member setUsername(String username) {
        this.username = username;
        return this;
    }
}
