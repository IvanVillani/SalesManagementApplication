package com.ivan.salesapp.enums;

import java.util.HashMap;
import java.util.Map;

public enum UserRole {
    CLIENT("client"), RESELLER("reseller"), ADMIN("admin"), ROOT("root");

    private String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getRole() {
        return value;
    }

    private static final Map<String, UserRole> lookup = new HashMap<>();

    static {
        for(UserRole role : UserRole.values())
        {
            lookup.put(role.getRole(), role);
        }
    }

    public static UserRole get(String role)
    {
        return lookup.get(role);
    }

    @Override
    public String toString() {
        return "ROLE_" + this.name();
    }
}
