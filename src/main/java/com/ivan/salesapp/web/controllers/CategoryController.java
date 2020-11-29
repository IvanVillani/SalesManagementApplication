package com.ivan.salesapp.web.controllers;

import com.ivan.salesapp.domain.models.binding.CategoryAddBindingModel;
import com.ivan.salesapp.domain.models.service.CategoryServiceModel;
import com.ivan.salesapp.domain.models.view.CategoryViewModel;
import com.ivan.salesapp.services.ICategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/categories")
public class CategoryController extends BaseController{
    private final ICategoryService ICategoryService;
    private final ModelMapper modelMapper;

    @Autowired
    public CategoryController(ICategoryService ICategoryService, ModelMapper modelMapper) {
        this.ICategoryService = ICategoryService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView addCategory(){
        return super.view("category/add-category");
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView addCategoryConfirm(@ModelAttribute CategoryAddBindingModel model){
        this.ICategoryService.addCategory(this.modelMapper.map(model, CategoryServiceModel.class));

        return super.redirect("/categories/all");
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView allCategories(ModelAndView modelAndView){
        modelAndView.addObject("categories",
                this.ICategoryService.findAllCategories().stream()
                        .map(c -> this.modelMapper
                                .map(c, CategoryViewModel.class))
                        .collect(Collectors.toList()));

        return super.view("/category/all-categories", modelAndView);
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView editCategory(@PathVariable String  id, ModelAndView modelAndView){
        modelAndView.addObject("model", this.modelMapper
                .map(this.ICategoryService.findCategoryById(id), CategoryViewModel.class));

        return super.view("/category/edit-category", modelAndView);
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView editCategoryConfirm(@PathVariable String id, @ModelAttribute CategoryAddBindingModel model){
        this.ICategoryService.editCategory(id, this.modelMapper.map(model, CategoryServiceModel.class));

        return super.redirect("/categories/all");
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView deleteCategory(@PathVariable String  id, ModelAndView modelAndView){
        modelAndView.addObject("model", this.modelMapper
                .map(this.ICategoryService.findCategoryById(id), CategoryViewModel.class));

        return super.view("/category/delete-category", modelAndView);
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView deleteCategoryConfirm(@PathVariable String id){
        this.ICategoryService.deleteCategory(id);

        return super.redirect("/categories/all");
    }

    @GetMapping("/fetch")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    @ResponseBody
    public List<CategoryViewModel> fetchCategories(){
        return this.ICategoryService.findAllCategories().stream()
                .map(c -> this.modelMapper
                .map(c, CategoryViewModel.class))
                .collect(Collectors.toList());
    }
}
