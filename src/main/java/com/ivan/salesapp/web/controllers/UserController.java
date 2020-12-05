package com.ivan.salesapp.web.controllers;

import com.ivan.salesapp.domain.models.binding.UserEditBindingModel;
import com.ivan.salesapp.domain.models.binding.UserRegisterBindingModel;
import com.ivan.salesapp.domain.models.service.UserServiceModel;
import com.ivan.salesapp.domain.models.view.UserAllViewModel;
import com.ivan.salesapp.domain.models.view.UserProfileViewModel;
import com.ivan.salesapp.services.IUserService;
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
public class UserController extends BaseController {
    private final IUserService iUserService;
    private final ModelMapper modelMapper;

    @Autowired
    public UserController(IUserService iUserService, ModelMapper modelMapper) {
        this.iUserService = iUserService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/register")
    @PreAuthorize("isAnonymous()")
    public ModelAndView register() {
        return super.view("user/register");
    }

    @PostMapping("/register")
    @PreAuthorize("isAnonymous()")
    public ModelAndView registerConfirm(@ModelAttribute UserRegisterBindingModel model) {
        if (!model.getPassword().equals(model.getConfirmPassword())) {
            return super.view("user/register");
        }
        this.iUserService.registerUser(this.modelMapper.map(model, UserServiceModel.class));

        return redirect("/users/login");
    }

    @GetMapping("/login")
    @PreAuthorize("isAnonymous()")
    public ModelAndView login() {
        return super.view("user/login");
    }

    @GetMapping("/login/order{id}")
    @PreAuthorize("isAnonymous()")
    public ModelAndView login(@PathVariable String id, ModelAndView modelAndView) {
        modelAndView.addObject("productId", id);

        return super.view("user/login", modelAndView);
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView profile(Principal principal, ModelAndView modelAndView) {
        modelAndView.addObject("model", this.modelMapper
                .map(this.iUserService.findUserByUsername(principal.getName()), UserProfileViewModel.class));
        return super.view("user/profile", modelAndView);
    }

    @GetMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView editProfile(Principal principal, ModelAndView modelAndView) {
        modelAndView.addObject("model", this.modelMapper
                .map(this.iUserService.findUserByUsername(principal.getName()), UserProfileViewModel.class));
        return super.view("user/edit-profile", modelAndView);
    }

    @PatchMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView editProfileConfirm(@ModelAttribute UserEditBindingModel model) {
        if (!model.getPassword().equals(model.getConfirmPassword())) {
            return super.view("user/edit-profile");
        }
        this.iUserService.editUserProfile(this.modelMapper.map(model, UserServiceModel.class), model.getOldPassword());
        return super.redirect("/users/profile");
    }

    @PostMapping("/delete-client{id}")
    @PreAuthorize("hasRole('ROLE_RESELLER') || hasRole('ROLE_ADMIN')")
    public ModelAndView deleteClient(@PathVariable String id) {
        this.iUserService.deleteUserById(id);

        return super.redirect("/users/all/clients");
    }

    @PostMapping("/delete-reseller{id}")
    @PreAuthorize("hasRole('ROLE_Admin')")
    public ModelAndView deleteReseller(@PathVariable String id) {
        this.iUserService.deleteUserById(id);

        return super.redirect("/users/all/resellers");
    }

    @PostMapping("/delete-admin{id}")
    @PreAuthorize("hasRole('ROLE_ROOT')")
    public ModelAndView deleteAdmin(@PathVariable String id) {
        this.iUserService.deleteUserById(id);

        return super.redirect("/users/all");
    }

    @PostMapping("/delete-me")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView deleteCurrentProfile(Principal principal) {
        this.iUserService.deleteUserByUsername(principal.getName());

        return super.redirect("/");
    }

    @GetMapping("/all/clients")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView allClients(ModelAndView modelAndView) {
        List<UserAllViewModel> usersList = iUserService
                .getUsersBasedOnAuthority("ROLE_CLIENT");

        modelAndView.addObject("users", Objects.requireNonNullElseGet(usersList, ArrayList::new));

        modelAndView.addObject("title", "Clients");

        return super.view("user/all-users", modelAndView);
    }

    @GetMapping("/all/resellers")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView allResellers(ModelAndView modelAndView) {
        List<UserAllViewModel> usersList = iUserService
                .getUsersBasedOnAuthority("ROLE_RESELLER");

        modelAndView.addObject("users", Objects.requireNonNullElseGet(usersList, ArrayList::new));

        modelAndView.addObject("title", "Resellers");

        return super.view("user/all-users", modelAndView);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ROOT')")
    public ModelAndView allUsers(ModelAndView modelAndView) {
        List<UserAllViewModel> usersList = iUserService
                .getUsersBasedOnAuthority(null);

        modelAndView.addObject("users", Objects.requireNonNullElseGet(usersList, ArrayList::new));

        modelAndView.addObject("title", "Users");

        return super.view("user/all-users", modelAndView);
    }

    @PostMapping("/set-client/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_RESELLER')")
    public ModelAndView setUser(@PathVariable String id) {
        this.iUserService.setUserRole(id, "client");

        return super.redirect("/users/all/clients");
    }

    @PostMapping("/set-reseller/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView setModerator(@PathVariable String id) {
        this.iUserService.setUserRole(id, "reseller");

        return super.redirect("/users/all/resellers");
    }

    @PostMapping("/set-admin/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView setAdmin(@PathVariable String id) {
        this.iUserService.setUserRole(id, "admin");

        return super.redirect("/users/all/resellers");
    }
}
