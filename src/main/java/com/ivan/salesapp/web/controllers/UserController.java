package com.ivan.salesapp.web.controllers;

import com.ivan.salesapp.domain.models.binding.UserEditBindingModel;
import com.ivan.salesapp.domain.models.binding.UserRegisterBindingModel;
import com.ivan.salesapp.domain.models.service.RoleServiceModel;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

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

    @PostMapping("/delete-user{id}")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView deleteUser(@PathVariable String id, ModelAndView modelAndView) {
        this.iUserService.deleteUserById(id);

        return super.redirect("/users/all");
    }

    @GetMapping("/all")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView allUsers(Principal principal, ModelAndView modelAndView) {
        System.out.println(getAuthorityOfPrincipal(principal, iUserService));
        List<UserAllViewModel> usersList = iUserService
                .getUsersBasedOnAuthority(getAuthorityOfPrincipal(principal, iUserService));

        if (usersList != null){
            modelAndView.addObject("users", usersList);
        }

        return super.view("user/all-users", modelAndView);
    }

    @PostMapping("/set-user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView setUser(@PathVariable String id) {
        this.iUserService.setUserRole(id, "user");

        return super.redirect("/users/all");
    }

    @PostMapping("/set-moderator/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView setModerator(@PathVariable String id) {
        this.iUserService.setUserRole(id, "moderator");

        return super.redirect("/users/all");
    }

    @PostMapping("/set-admin/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView setAdmin(@PathVariable String id) {
        this.iUserService.setUserRole(id, "admin");

        return super.redirect("/users/all");
    }

    private static String getAuthorityOfPrincipal(Principal principal, IUserService iUserService) {
        List<RoleServiceModel> authorities = new ArrayList<>(iUserService.findUserByUsername(principal.getName())
                .getAuthorities());

        if(authorities.size() > 1){
            return "ROLE_ROOT";
        }else{
            return authorities.get(0).getAuthority();
        }
    }
}
