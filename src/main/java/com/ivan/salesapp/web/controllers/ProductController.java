package com.ivan.salesapp.web.controllers;

import com.ivan.salesapp.domain.models.binding.ProductAddBindingModel;
import com.ivan.salesapp.domain.models.service.ProductServiceModel;
import com.ivan.salesapp.domain.models.view.ProductAllViewModel;
import com.ivan.salesapp.services.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/products")
public class ProductController extends BaseController{
    private final IProductService iProductService;
    private final ICategoryService iCategoryService;
    private final ICloudinaryService iCloudinaryService;
    private final ModelMapper modelMapper;

    @Autowired
    public ProductController(IProductService iProductService, ICategoryService iCategoryService, ICloudinaryService iCloudinaryService, ModelMapper modelMapper) {
        this.iProductService = iProductService;
        this.iCategoryService = iCategoryService;
        this.iCloudinaryService = iCloudinaryService;

        this.modelMapper = modelMapper;
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView addProduct(){
        //Doesn't need setting the view in ModelAndView, because of AJAX (using for fetch in add-product.html)
        return super.view("product/add-product");
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView addProductConfirm(@ModelAttribute ProductAddBindingModel model) throws IOException {
        ProductServiceModel productServiceModel = this.modelMapper.map(model, ProductServiceModel.class);

        productServiceModel.setCategories(
                this.iCategoryService.findAllCategories().stream()
                        .filter(c -> model.getCategories().contains(c.getId()))
                        .collect(Collectors.toList())
        );

        productServiceModel.setImageUrl(this.iCloudinaryService.uploadImage(model.getImage()));

        this.iProductService.addProduct(productServiceModel);

        return super.redirect("/products/all");
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView allProducts(ModelAndView modelAndView) throws IOException {
        modelAndView.addObject("products", this.iProductService.findAllProducts()
                .stream().map(p ->
                        this.modelMapper.map(p, ProductAllViewModel.class))
                .collect(Collectors.toList()));

        return super.view("product/all-products", modelAndView);
    }
}
