package com.ivan.salesapp.domain.models.service;


import com.ivan.salesapp.domain.entities.Order;

import java.util.Set;

public class UserServiceModel extends BaseServiceModel{
    private String username;
    private String password;
    private String email;

    private Set<RoleServiceModel> authorities;

    private Set<Order> orders;

    public UserServiceModel() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<RoleServiceModel> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<RoleServiceModel> authorities) {
        this.authorities = authorities;
    }
}
