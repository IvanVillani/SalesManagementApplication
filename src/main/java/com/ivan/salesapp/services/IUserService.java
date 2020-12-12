package com.ivan.salesapp.services;

import com.ivan.salesapp.domain.models.service.UserServiceModel;
import com.ivan.salesapp.domain.models.view.UserAllViewModel;
import com.ivan.salesapp.exceptions.InvalidUserException;
import com.ivan.salesapp.exceptions.UserNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface IUserService extends UserDetailsService {
    UserServiceModel registerUser(UserServiceModel userServiceModel);

    UserServiceModel findUserByUsername(String username);

    UserServiceModel editUserProfile(UserServiceModel userServiceModel, String oldPassword) throws InvalidUserException;

    List<UserServiceModel> findAllUsers();

    UserServiceModel findUserById(String id);

    void deleteUserById(String id) throws UserNotFoundException;

    void deleteUserByUsername(String username) throws UserNotFoundException;

    UserServiceModel setUserRole(String id, String role) throws UserNotFoundException;

    List<UserAllViewModel> getUsersBasedOnAuthority(String authority);

}
