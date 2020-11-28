package com.ivan.salesapp.web.controllers;

import com.ivan.salesapp.services.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/products")
public class ProductController extends BaseController{
    private final ProductService productService;
    private final ModelMapper modelMapper;

    @Autowired
    public ProductController(ProductService productService, ModelMapper modelMapper) {
        this.productService = productService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView addProduct(){
        //Doesn't need setting the view in ModelAndView, because of AJAX (using for fetch in add-product.html)
        return super.view("product/add-product");
    }

//    @PostMapping("/add")
//    @PreAuthorize("hasRole('ROLE_MODERATOR')")
//    public ModelAndView addProductConfirm(@ModelAttribute Pro){
//        this.productService.addProduct()
//        //Doesn't need setting the view in ModelAndView, because of AJAX (using for fetch in add-product.html)
//        return super.view("product/add-product");
//    }
}
