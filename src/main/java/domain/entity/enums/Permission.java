package domain.entity.enums;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum Permission {
    // Own messages
    WRITE, MEDIA, REMOVE, WRITE_SILENCED,

    // Other messages
    READ, REMOVE_OTHER,

    // Users
    RENAME_SELF, RENAME_OTHER,
    REMOVE_USER, BAN_USER,
    MANAGE_USER_ROLE,

    // Administration
    MANAGE_ROLES, CLOSE, SILENCE,
    UPDATE, DELETE;

    public static Set<Permission> all() {
        return new HashSet<>(Arrays.asList(Permission.values()));
    }

    public static Set<Permission> user() {
        return new HashSet<>(Arrays.asList(
                Permission.WRITE, Permission.MEDIA, Permission.READ,
                Permission.REMOVE, Permission.RENAME_SELF
        ));
    }

    public static Set<Permission> moderator() {
        return new HashSet<>(Arrays.asList(
                Permission.REMOVE_OTHER, Permission.WRITE_SILENCED,
                Permission.REMOVE_USER, Permission.RENAME_OTHER
        ));
    }

    public static Set<Permission> administrator() {
        return new HashSet<>(Arrays.asList(
                Permission.BAN_USER, Permission.MANAGE_USER_ROLE,
                Permission.MANAGE_ROLES, Permission.UPDATE,
                Permission.SILENCE
        ));
    }
}
