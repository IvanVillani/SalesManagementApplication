package com.ivan.salesapp.web.controllers;

import com.ivan.salesapp.domain.models.view.UserProfileViewModel;
import com.ivan.salesapp.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
public class HomeController extends BaseController{
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public HomeController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/")
    @PreAuthorize("isAnonymous()")
    public ModelAndView index(){
        return super.view("index");
    }

    @GetMapping("/home")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView home(Principal principal, ModelAndView modelAndView){
        modelAndView.addObject("model", this.modelMapper
                .map(this.userService.findUserByUsername(principal.getName()), UserProfileViewModel.class));
        return super.view("home", modelAndView);
    }
}
