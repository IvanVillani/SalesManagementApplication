package com.ivan.salesapp.constants;

public interface RoleConstants {
    String ROLE_CLIENT = "hasRole('ROLE_CLIENT')";
    String ROLE_RESELLER = "hasRole('ROLE_RESELLER')";
    String ROLE_ADMIN = "hasRole('ROLE_ADMIN')";
    String ROLE_ROOT = "hasRole('ROLE_ROOT')";
    String ROLE_RESELLER_OR_ADMIN= "hasRole('ROLE_RESELLER') || hasRole('ROLE_ADMIN')";
}
