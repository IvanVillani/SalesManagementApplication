package com.ivan.salesapp.enums;

import java.util.HashMap;
import java.util.Map;

public enum RoleEnum {
    CLIENT("client"), RESELLER("reseller"), ADMIN("admin"), ROOT("root");

    private String value;

    RoleEnum(String value) {
        this.value = value;
    }

    public String getSmall() {
        return value;
    }

    private static final Map<String, RoleEnum> lookup = new HashMap<>();

    static {
        for(RoleEnum role : RoleEnum.values())
        {
            lookup.put(role.getSmall(), role);
        }
    }

    public static RoleEnum get(String role)
    {
        return lookup.get(role);
    }

    @Override
    public String toString() {
        return "ROLE_" + this.name();
    }
}
