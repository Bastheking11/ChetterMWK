package domain.entity;

import domain.entity.enums.Permission;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.Set;

@Entity
@Table
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 20)
    @Pattern(regexp = "[A-Za-z0-9_-]{3,20}")
    private String name;

    @Column(length = 6)
    @Pattern(regexp = "[A-H0-9]{6}")
    private String color;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Permission.class)
    private Set<Permission> permissions;

    @Column(nullable = false)
    private int index;

    @ManyToOne
    private Party party;

    public Role(int index, String name, String color, Set<Permission> permissions, Party party) {
        setIndex(index);
        setName(name);
        setColor(color);
        setPermissions(permissions);
        setParty(party);
    }

    public Role() {
    }

    public int getIndex() {
        return index;
    }

    public Role setIndex(int index) {
        this.index = index;
        return this;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Role setName(String name) {
        this.name = name;
        return this;
    }

    public String getColor() {
        return color;
    }

    public Role setColor(String color) {
        this.color = color;
        return this;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Role setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
        return this;
    }

    public Role addPermissions(Set<Permission> permissions) {
        this.permissions.addAll(permissions);
        return this;
    }

    public Role addPermission(Permission permission) {
        this.permissions.add(permission);
        return this;
    }

    public Role removePermissions(Set<Permission> permissions) {
        this.permissions.removeAll(permissions);
        return this;
    }

    public Role removePermission(Permission permission) {
        this.permissions.remove(permission);
        return this;
    }

    public Party getParty() {
        return party;
    }

    public Role setParty(Party party) {
        this.party = party;

        return this;
    }
}
