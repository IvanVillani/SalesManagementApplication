package com.ivan.salesapp.web.controllers;

import com.ivan.salesapp.constants.RoleConstants;
import com.ivan.salesapp.constants.ViewConstants;
import com.ivan.salesapp.domain.models.binding.CategoryAddBindingModel;
import com.ivan.salesapp.domain.models.service.CategoryServiceModel;
import com.ivan.salesapp.domain.models.view.CategoryViewModel;
import com.ivan.salesapp.services.ICategoryService;
import com.ivan.salesapp.services.IProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/categories")
public class CategoryController extends BaseController implements RoleConstants, ViewConstants {
    private final ICategoryService iCategoryService;
    private final IProductService iProductService;
    private final ModelMapper modelMapper;

    @Autowired
    public CategoryController(ICategoryService iCategoryService, IProductService iProductService, ModelMapper modelMapper) {
        this.iCategoryService = iCategoryService;
        this.iProductService = iProductService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/add")
    @PreAuthorize(ROLE_ADMIN)
    public ModelAndView addCategory() {
        return super.view(CATEGORY_ADD);
    }

    @PostMapping("/add")
    @PreAuthorize(ROLE_ADMIN)
    public ModelAndView addCategoryConfirm(@ModelAttribute CategoryAddBindingModel model) {
        this.iCategoryService.addCategory(this.modelMapper.map(model, CategoryServiceModel.class));

        return super.redirect("/categories/all");
    }

    @GetMapping("/all")
    @PreAuthorize(ROLE_RESELLER_OR_ADMIN)
    public ModelAndView allCategories(ModelAndView modelAndView) {
        modelAndView.addObject("categories",
                this.iCategoryService.findAllCategories()
                        .stream()
                        .map(c -> this.modelMapper.map(c, CategoryViewModel.class))
                        .collect(toList()));

        return super.view(CATEGORY_ALL, modelAndView);
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize(ROLE_RESELLER)
    public ModelAndView editCategory(@PathVariable String id, ModelAndView modelAndView) {
        modelAndView.addObject("model", this.modelMapper
                .map(this.iCategoryService.findCategoryById(id), CategoryViewModel.class));

        return super.view(CATEGORY_EDIT, modelAndView);
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize(ROLE_RESELLER)
    public ModelAndView editCategoryConfirm(@PathVariable String id, @ModelAttribute CategoryAddBindingModel model) {
        this.iCategoryService.editCategory(id, this.modelMapper.map(model, CategoryServiceModel.class));

        return super.redirect("/categories/all");
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize(ROLE_RESELLER)
    public ModelAndView deleteCategory(@PathVariable String id, ModelAndView modelAndView) {
        modelAndView.addObject("model", this.modelMapper
                .map(this.iCategoryService.findCategoryById(id), CategoryViewModel.class));

        return super.view(CATEGORY_DELETE, modelAndView);
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize(ROLE_RESELLER)
    public ModelAndView deleteCategoryConfirm(@PathVariable String id) {
        this.iCategoryService.deleteCategory(id, this.iProductService);

        return super.redirect("/categories/all");
    }

    @GetMapping("/fetch")
    @PreAuthorize(ROLE_ADMIN)
    @ResponseBody
    public List<CategoryViewModel> fetchCategories() {
        return this.iCategoryService.findAllCategories()
                .stream()
                .map(c -> this.modelMapper.map(c, CategoryViewModel.class))
                .collect(toList());
    }
}
