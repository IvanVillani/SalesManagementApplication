package com.ivan.salesapp.services;

import com.ivan.salesapp.domain.models.service.UserServiceModel;
import com.ivan.salesapp.domain.models.view.UserAllViewModel;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface IUserService extends UserDetailsService {
    UserServiceModel registerUser(UserServiceModel userServiceModel);

    UserServiceModel findUserByUsername(String username);

    UserServiceModel editUserProfile(UserServiceModel userServiceModel, String oldPassword);

    List<UserServiceModel> findAllUsers();

    UserServiceModel findUserById(String id);

    void deleteUserById(String id);

    void setUserRole(String id, String role);

    List<UserAllViewModel> getUsersBasedOnAuthority(String authority);

}
