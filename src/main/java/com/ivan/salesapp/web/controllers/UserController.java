package com.ivan.salesapp.web.controllers;

import com.ivan.salesapp.constants.RoleConstants;
import com.ivan.salesapp.constants.ViewConstants;
import com.ivan.salesapp.domain.models.binding.UserEditBindingModel;
import com.ivan.salesapp.domain.models.binding.UserRegisterBindingModel;
import com.ivan.salesapp.domain.models.service.UserServiceModel;
import com.ivan.salesapp.domain.models.view.UserAllViewModel;
import com.ivan.salesapp.domain.models.view.UserProfileViewModel;
import com.ivan.salesapp.enums.UserRole;
import com.ivan.salesapp.exceptions.InvalidUserException;
import com.ivan.salesapp.exceptions.UserNotFoundException;
import com.ivan.salesapp.services.IUserService;
import com.ivan.salesapp.services.validation.IUserValidationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/users")
public class UserController extends BaseController implements RoleConstants, ViewConstants {
    private final IUserService iUserService;
    private final ModelMapper modelMapper;
    private final IUserValidationService iUserValidationService;

    @Autowired
    public UserController(IUserService iUserService, ModelMapper modelMapper, IUserValidationService iUserValidationService) {
        this.iUserService = iUserService;
        this.modelMapper = modelMapper;
        this.iUserValidationService = iUserValidationService;
    }

    @GetMapping("/register")
    @PreAuthorize("isAnonymous()")
    public ModelAndView register() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("error", "");
        return super.view(USER_REGISTER, modelAndView);
    }

    @PostMapping("/register")
    @PreAuthorize("isAnonymous()")
    public ModelAndView registerConfirm(@ModelAttribute UserRegisterBindingModel model) throws InvalidUserException {
        this.iUserValidationService.isNewUserValid(model, USER_REGISTER);

        this.iUserService.registerUser(this.modelMapper.map(model, UserServiceModel.class));

        return redirect("/users/login");
    }

    @GetMapping("/login")
    @PreAuthorize("isAnonymous()")
    public ModelAndView login() {
        return super.view(USER_LOGIN);
    }

    @GetMapping("/login/order{id}")
    @PreAuthorize("isAnonymous()")
    public ModelAndView login(@PathVariable String id, ModelAndView modelAndView) {
        modelAndView.addObject("productId", id);

        return super.view(USER_LOGIN, modelAndView);
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView profile(Principal principal, ModelAndView modelAndView) {
        modelAndView.addObject("model", this.modelMapper
                .map(this.iUserService.findUserByUsername(principal.getName()), UserProfileViewModel.class));
        modelAndView.addObject("error", "");
        return super.view(USER_PROFILE, modelAndView);
    }

    @GetMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView editProfile(Principal principal, ModelAndView modelAndView) {
        modelAndView.addObject("model", this.modelMapper
                .map(this.iUserService.findUserByUsername(principal.getName()), UserProfileViewModel.class));
        return super.view(USER_EDIT, modelAndView);
    }

    @PostMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView editProfileConfirm(@ModelAttribute UserEditBindingModel model) throws InvalidUserException {
        this.iUserValidationService.isEditedUserValid(model, USER_EDIT);

        this.iUserService.editUserProfile(this.modelMapper.map(model, UserServiceModel.class), model.getOldPassword());
        return super.redirect("/users/profile");
    }

    @PostMapping("/delete-client{id}")
    @PreAuthorize(ROLE_RESELLER_OR_ADMIN)
    public ModelAndView deleteClient(@PathVariable String id) throws UserNotFoundException {
        this.iUserService.deleteUserById(id);

        return super.redirect("/users/all/clients");
    }

    @PostMapping("/delete-reseller{id}")
    @PreAuthorize(ROLE_ADMIN)
    public ModelAndView deleteReseller(@PathVariable String id) throws UserNotFoundException {
        this.iUserService.deleteUserById(id);

        return super.redirect("/users/all/resellers");
    }

    @PostMapping("/delete-admin{id}")
    @PreAuthorize(ROLE_ROOT)
    public ModelAndView deleteAdmin(@PathVariable String id) throws UserNotFoundException {
        this.iUserService.deleteUserById(id);

        return super.redirect("/users/all");
    }

    @PostMapping("/delete-me")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView deleteCurrentProfile(Principal principal) throws UserNotFoundException {
        this.iUserService.deleteUserByUsername(principal.getName());

        return super.redirect("/");
    }

    @GetMapping("/all/clients")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView allClients(ModelAndView modelAndView) {
        List<UserAllViewModel> usersList = iUserService
                .getUsersBasedOnAuthority(UserRole.CLIENT.toString());

        modelAndView.addObject("users", Objects.requireNonNullElseGet(usersList, ArrayList::new));

        modelAndView.addObject("title", "Clients");

        return super.view(USER_ALL, modelAndView);
    }

    @GetMapping("/all/resellers")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView allResellers(ModelAndView modelAndView) {
        List<UserAllViewModel> usersList = iUserService
                .getUsersBasedOnAuthority(UserRole.RESELLER.toString());

        modelAndView.addObject("users", Objects.requireNonNullElseGet(usersList, ArrayList::new));

        modelAndView.addObject("title", "Resellers");

        return super.view(USER_ALL, modelAndView);
    }

    @GetMapping("/all")
    @PreAuthorize(ROLE_ROOT)
    public ModelAndView allUsers(ModelAndView modelAndView) {
        List<UserAllViewModel> usersList = iUserService
                .getUsersBasedOnAuthority(null);

        modelAndView.addObject("users", Objects.requireNonNullElseGet(usersList, ArrayList::new));

        modelAndView.addObject("title", "Users");

        return super.view(USER_ALL, modelAndView);
    }

    @PostMapping("/set-client/{id}")
    @PreAuthorize(ROLE_RESELLER_OR_ADMIN)
    public ModelAndView setUser(@PathVariable String id) throws UserNotFoundException {
        this.iUserService.setUserRole(id, UserRole.CLIENT.getRole());

        return super.redirect("/users/all/clients");
    }

    @PostMapping("/set-reseller/{id}")
    @PreAuthorize(ROLE_ADMIN)
    public ModelAndView setModerator(@PathVariable String id) throws UserNotFoundException {
        this.iUserService.setUserRole(id, UserRole.RESELLER.getRole());

        return super.redirect("/users/all/resellers");
    }

    @PostMapping("/set-admin/{id}")
    @PreAuthorize(ROLE_ADMIN)
    public ModelAndView setAdmin(@PathVariable String id) throws UserNotFoundException {
        this.iUserService.setUserRole(id, UserRole.ADMIN.getRole());

        return super.redirect("/users/all/resellers");
    }
}
