package com.ivan.salesapp.services;

import com.ivan.salesapp.constants.ExceptionMessageConstants;
import com.ivan.salesapp.constants.MailSenderConstants;
import com.ivan.salesapp.constants.ViewConstants;
import com.ivan.salesapp.domain.entities.Role;
import com.ivan.salesapp.domain.entities.User;
import com.ivan.salesapp.domain.models.service.RoleServiceModel;
import com.ivan.salesapp.domain.models.service.UserServiceModel;
import com.ivan.salesapp.domain.models.view.UserAllViewModel;
import com.ivan.salesapp.enums.UserRole;
import com.ivan.salesapp.exceptions.InvalidUserException;
import com.ivan.salesapp.exceptions.UserNotFoundException;
import com.ivan.salesapp.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
public class UserService implements IUserService, ExceptionMessageConstants, ViewConstants, MailSenderConstants {
    private final UserRepository userRepository;
    private final IDiscountService iDiscountService;
    private final IRoleService iRoleService;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, IDiscountService iDiscountService, IRoleService iRoleService, ModelMapper modelMapper, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.iDiscountService = iDiscountService;
        this.iRoleService = iRoleService;
        this.modelMapper = modelMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserServiceModel registerUser(UserServiceModel userServiceModel) {
        this.iRoleService.seedRolesInDB();
        if (this.userRepository.count() == 0) {
            userServiceModel.setAuthorities(this.iRoleService.findAllRoles());
        } else {
            userServiceModel.setAuthorities(new LinkedHashSet<>());

            userServiceModel.getAuthorities().add(this.iRoleService.findByAuthority(UserRole.CLIENT.toString()));
        }

        User user = this.modelMapper.map(userServiceModel, User.class);
        user.setPassword(this.bCryptPasswordEncoder.encode(userServiceModel.getPassword()));

        this.userRepository.saveAndFlush(user);

        return this.modelMapper.map(user, UserServiceModel.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_BY_USERNAME));
    }

    @Override
    public UserServiceModel findUserByUsername(String username) {
        return this.userRepository.findByUsername(username).map(u -> this.modelMapper
                .map(u, UserServiceModel.class))
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_BY_USERNAME));
    }

    @Override
    public UserServiceModel editUserProfile(UserServiceModel userServiceModel, String oldPassword) throws InvalidUserException {
        User user = this.userRepository.findByUsername(userServiceModel.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_BY_USERNAME));
        if (!this.bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidUserException(INCORRECT_PASSWORD, userServiceModel.getUsername(), USER_EDIT);
        }

        user.setPassword(!"".equals(userServiceModel.getPassword()) ?
                this.bCryptPasswordEncoder.encode(userServiceModel.getPassword()) :
                user.getPassword());

        user.setEmail(userServiceModel.getEmail());

        this.userRepository.saveAndFlush(user);

        return this.modelMapper.map(user, UserServiceModel.class);
    }

    @Override
    public void deleteUserById(String id) throws UserNotFoundException {
        User user = this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_BY_ID));

        iDiscountService.deleteDiscountsByUserId(user.getId());

        this.userRepository.delete(user);
    }

    @Override
    public void deleteUserByUsername(String username) throws UserNotFoundException {
        User user = this.userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_BY_USERNAME));

        iDiscountService.deleteDiscountsByUserId(user.getId());

        this.userRepository.delete(user);
    }

    @Override
    public List<UserServiceModel> findAllUsers() {
        return this.userRepository.findAll()
                .stream()
                .map(u -> this.modelMapper.map(u, UserServiceModel.class)).collect(toList());
    }

    @Override
    public UserServiceModel findUserById(String id) {
        return this.userRepository.findAll()
                .stream()
                .filter(u -> id.equals(u.getId()))
                .map(u -> this.modelMapper.map(u, UserServiceModel.class))
                .collect(toList())
                .get(0);
    }

    @Override
    public UserServiceModel setUserRole(String id, String role) throws UserNotFoundException {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_BY_ID));

        UserServiceModel userServiceModel = this.modelMapper.map(user, UserServiceModel.class);

        UserRole authority = UserRole.get(role);

        checkIfNewAdminAndDeleteOld(authority);

        userServiceModel.getAuthorities().clear();

        userServiceModel.getAuthorities().add(this.iRoleService.findByAuthority(authority.toString()));

        this.userRepository.saveAndFlush(this.modelMapper.map(userServiceModel, User.class));

        return userServiceModel;
    }

    @Override
    public List<UserAllViewModel> getUsersBasedOnAuthority(String authority) {
        if (authority != null) {
            return this.findAllUsers()
                    .stream()
                    .filter(u -> new ArrayList<>(u.getAuthorities()).size() == 1)
                    .filter(u -> new ArrayList<>(u.getAuthorities()).get(0).getAuthority().equals(authority))
                    .map(mapToViewModelSetCategories(this.modelMapper))
                    .collect(toList());
        }
        return this.findAllUsers()
                .stream()
                .map(mapToViewModelSetCategories(this.modelMapper))
                .collect(toList());

    }

    private Function<UserServiceModel, UserAllViewModel> mapToViewModelSetCategories(ModelMapper modelMapper) {
        return u -> {
            UserAllViewModel user = modelMapper.map(u, UserAllViewModel.class);
            if (u.getAuthorities().size() > 1) {
                user.setAuthorities(new LinkedHashSet<>());
                user.getAuthorities().add(UserRole.ROOT.getRole().toUpperCase());
            } else {
                user.setAuthorities(u.getAuthorities()
                        .stream()
                        .map(RoleServiceModel::getAuthority)
                        .map(s -> s.substring(5))
                        .collect(toSet()));
            }
            return user;
        };
    }

    private void checkIfNewAdminAndDeleteOld(UserRole newAuthority) throws UserNotFoundException {
        if(newAuthority.toString().equals(UserRole.ADMIN.toString())){
            List<User> users = this.userRepository.findAll();

            for (User user : users) {
                List<Role> roles = new ArrayList<>(user.getAuthorities());
                if (roles.size() == 1 && UserRole.ADMIN.toString().equals(roles.get(0).getAuthority())){
                    this.setUserRole(user.getId(), UserRole.RESELLER.getRole());
                    break;
                }
            }
        }
    }
}
