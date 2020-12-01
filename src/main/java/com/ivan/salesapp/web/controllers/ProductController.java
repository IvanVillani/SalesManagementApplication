package com.ivan.salesapp.web.controllers;

import com.ivan.salesapp.domain.models.binding.ProductAddBindingModel;
import com.ivan.salesapp.domain.models.binding.ProductDeleteBindingModel;
import com.ivan.salesapp.domain.models.binding.ProductEditBindingModel;
import com.ivan.salesapp.domain.models.service.BaseServiceModel;
import com.ivan.salesapp.domain.models.service.CategoryServiceModel;
import com.ivan.salesapp.domain.models.service.ProductServiceModel;
import com.ivan.salesapp.domain.models.view.ProductAllViewModel;
import com.ivan.salesapp.domain.models.view.ProductDetailsViewModel;
import com.ivan.salesapp.services.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Controller
@RequestMapping("/products")
public class ProductController extends BaseController {
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
    public ModelAndView addProduct() {
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
                        .collect(toList())
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
                .collect(toList()));

        return super.view("product/all-products", modelAndView);
    }

    @GetMapping("/details/{id}")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView detailsProduct(@PathVariable String id, ModelAndView modelAndView) {
        modelAndView.addObject("product", this.modelMapper
                .map(this.iProductService.findProductById(id), ProductDetailsViewModel.class));

        return super.view("product/details", modelAndView);
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView editProduct(@PathVariable String id, ModelAndView modelAndView) {
        ProductServiceModel productServiceModel = this.iProductService.findProductById(id);
        ProductAddBindingModel model = this.modelMapper.map(productServiceModel, ProductAddBindingModel.class);

        model.setCategories(productServiceModel.getCategories()
                .stream()
                .map(BaseServiceModel::getId)
                .collect(toList()));

        modelAndView.addObject("product", model);
        modelAndView.addObject("productId", id);

        return super.view("product/edit-product", modelAndView);
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView editProductConfirm(@PathVariable String id, @ModelAttribute ProductEditBindingModel productEditBindingModel) {
        this.iProductService.editProduct(
                id,
                this.modelMapper.map(productEditBindingModel, ProductServiceModel.class),
                productEditBindingModel.getCategories()
        );

        return super.redirect("/products/details/" + id);
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView deleteProduct(@PathVariable String id, ModelAndView modelAndView) {
        ProductServiceModel productServiceModel = this.iProductService.findProductById(id);
        ProductDeleteBindingModel model = this.modelMapper.map(productServiceModel, ProductDeleteBindingModel.class);

        model.setCategories(productServiceModel.getCategories()
                .stream()
                .map(CategoryServiceModel::getName)
                .collect(toList()));

        modelAndView.addObject("product", model);
        modelAndView.addObject("categories", model);
        modelAndView.addObject("productId", id);

        return super.view("product/delete-product", modelAndView);
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView deleteProductConfirm(@PathVariable String id) {
        this.iProductService.deleteProduct(id);

        return super.redirect("/products/all");
    }

    @GetMapping("/fetch/{category}")
    @ResponseBody
    public List<ProductAllViewModel> fetchByCategory(@PathVariable String category) {
        if (category.equals("all")) {
            return this.iProductService.findAllProducts()
                    .stream()
                    .map(product -> this.modelMapper.map(product, ProductAllViewModel.class))
                    .collect(toList());
        }

        return this.iProductService.findAllByCategory(category)
                .stream()
                .map(product -> this.modelMapper.map(product, ProductAllViewModel.class))
                .collect(toList());
    }
}
