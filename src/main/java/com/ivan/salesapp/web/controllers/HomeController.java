package com.ivan.salesapp.web.controllers;

import com.ivan.salesapp.domain.models.view.CategoryViewModel;
import com.ivan.salesapp.services.ICategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
public class HomeController extends BaseController{
    private final ICategoryService iCategoryService;
    private final ModelMapper modelMapper;

    @Autowired
    public HomeController(ICategoryService iCategoryService, ModelMapper modelMapper) {
        this.iCategoryService = iCategoryService;
        this.modelMapper = modelMapper;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView index(ModelAndView modelAndView){
        List<CategoryViewModel> categories = this.iCategoryService.findAllCategories()
                .stream()
                .map(category -> this.modelMapper.map(category, CategoryViewModel.class))
                .collect(toList());

        modelAndView.addObject("categories", categories);

        if (checkIfAuthenticated()){
            return super.view("home", modelAndView);
        }

        return super.view("index", modelAndView);
    }

    @GetMapping("/home")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView home(ModelAndView modelAndView){
        List<CategoryViewModel> categories = this.iCategoryService.findAllCategories()
                .stream()
                .map(category -> this.modelMapper.map(category, CategoryViewModel.class))
                .collect(toList());

        modelAndView.addObject("categories", categories);

        return super.view("home", modelAndView);
    }

    private static Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private static boolean checkIfAuthenticated(){
        return getAuthentication() != null &&
                getAuthentication().isAuthenticated();
    }
}
