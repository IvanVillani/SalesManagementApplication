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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
public class UserController extends BaseController{
    private final IUserService IUserService;
    private final ModelMapper modelMapper;

    @Autowired
    public UserController(IUserService IUserService, ModelMapper modelMapper) {
        this.IUserService = IUserService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/register")
    @PreAuthorize("isAnonymous()")
    public ModelAndView register(){
        return super.view("user/register");
    }

    @PostMapping("/register")
    @PreAuthorize("isAnonymous()")
    public ModelAndView registerConfirm(@ModelAttribute UserRegisterBindingModel model){
        if (!model.getPassword().equals(model.getConfirmPassword())){
            return super.view("user/register");
        }
        this.IUserService.registerUser(this.modelMapper.map(model, UserServiceModel.class));

        return redirect("/users/login");
    }

    @GetMapping("/login")
    @PreAuthorize("isAnonymous()")
    public ModelAndView login(){
        return super.view("user/login");
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView profile(Principal principal, ModelAndView modelAndView){
        modelAndView.addObject("model", this.modelMapper
                .map(this.IUserService.findUserByUsername(principal.getName()), UserProfileViewModel.class));
        return super.view("user/profile", modelAndView);
    }

    @GetMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView editProfile(Principal principal, ModelAndView modelAndView){
        modelAndView.addObject("model", this.modelMapper
                .map(this.IUserService.findUserByUsername(principal.getName()), UserProfileViewModel.class));
        return super.view("user/edit-profile", modelAndView);
    }

    @PatchMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView editProfileConfirm(@ModelAttribute UserEditBindingModel model){
        if(!model.getPassword().equals(model.getConfirmPassword())){
            return super.view("user/edit-profile");
        }
        this.IUserService.editUserProfile(this.modelMapper.map(model, UserServiceModel.class), model.getOldPassword());
        return super.redirect("/users/profile");
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView allUsers(ModelAndView modelAndView){
        List<UserAllViewModel> users = this.IUserService.findAllUsers()
                .stream()
                .map(u -> {
                    UserAllViewModel user = this.modelMapper.map(u, UserAllViewModel.class);
                    user.setAuthorities(u.getAuthorities().stream().map(RoleServiceModel::getAuthority).collect(Collectors.toSet()));
                    return user;
                })
                .collect(Collectors.toList());

        modelAndView.addObject("users", users);

        return super.view("user/all-users", modelAndView);
    }

    @PostMapping("/set-user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView setUser(@PathVariable String id){
        this.IUserService.setUserRole(id, "user");

        return super.redirect("/users/all");
    }

    @PostMapping("/set-moderator/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView setModerator(@PathVariable String id){
        this.IUserService.setUserRole(id, "moderator");

        return super.redirect("/users/all");
    }

    @PostMapping("/set-admin/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView setAdmin(@PathVariable String id){
        this.IUserService.setUserRole(id, "admin");

        return super.redirect("/users/all");
    }
}
