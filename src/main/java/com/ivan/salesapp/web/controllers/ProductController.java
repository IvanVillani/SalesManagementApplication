package com.ivan.salesapp.web.controllers;

import com.ivan.salesapp.constants.RoleConstants;
import com.ivan.salesapp.constants.ViewConstants;
import com.ivan.salesapp.domain.models.binding.ProductAddBindingModel;
import com.ivan.salesapp.domain.models.binding.ProductDeleteBindingModel;
import com.ivan.salesapp.domain.models.binding.ProductEditBindingModel;
import com.ivan.salesapp.domain.models.service.BaseServiceModel;
import com.ivan.salesapp.domain.models.service.CategoryServiceModel;
import com.ivan.salesapp.domain.models.service.DiscountServiceModel;
import com.ivan.salesapp.domain.models.service.ProductServiceModel;
import com.ivan.salesapp.domain.models.view.DiscountViewModel;
import com.ivan.salesapp.domain.models.view.ProductAllViewModel;
import com.ivan.salesapp.domain.models.view.ProductDetailsViewModel;
import com.ivan.salesapp.domain.models.view.ShoppingCartItem;
import com.ivan.salesapp.services.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/products")
public class ProductController extends BaseController implements RoleConstants, ViewConstants {
    private final IProductService iProductService;
    private final ICategoryService iCategoryService;
    private final ICloudinaryService iCloudinaryService;
    private final IDiscountService iDiscountService;
    private final ModelMapper modelMapper;

    @Autowired
    public ProductController(IProductService iProductService, ICategoryService iCategoryService, ICloudinaryService iCloudinaryService, IDiscountService iDiscountService, ModelMapper modelMapper) {
        this.iProductService = iProductService;
        this.iCategoryService = iCategoryService;
        this.iCloudinaryService = iCloudinaryService;
        this.iDiscountService = iDiscountService;

        this.modelMapper = modelMapper;
    }

    @GetMapping("/add")
    @PreAuthorize(ROLE_ADMIN)
    public ModelAndView addProduct() {
        //Doesn't need setting the view in ModelAndView, because of AJAX (using for fetch in add-product.html)
        return super.view(PRODUCT_ADD);
    }

    @PostMapping("/add")
    @PreAuthorize(ROLE_ADMIN)
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
    @PreAuthorize(ROLE_RESELLER_OR_ADMIN)
    public ModelAndView allProducts(ModelAndView modelAndView) throws IOException {
        modelAndView.addObject("products", this.iProductService.findAllProducts()
                .stream()
                .map(p -> this.modelMapper.map(p, ProductAllViewModel.class))
                .collect(toList()));

        return super.view(PRODUCT_ALL, modelAndView);
    }

    @GetMapping("/details/{id}")
    public ModelAndView detailsProduct(@PathVariable String id, HttpSession session, ModelAndView modelAndView) {
        modelAndView.addObject("product", this.modelMapper
                .map(this.iProductService.findProductById(id), ProductDetailsViewModel.class));

        long cartQuantity = 0;

        List<ShoppingCartItem> cartItems = (List<ShoppingCartItem>) session.getAttribute("shopping-cart");

        if (cartItems != null) {
            for (ShoppingCartItem cartItem : cartItems) {
                if (id.equals(cartItem.getProduct().getProduct().getId())) {
                    cartQuantity = cartItem.getQuantity();
                }
            }
        }

        modelAndView.addObject("cartQuantity", cartQuantity);

        return super.view(PRODUCT_DETAILS, modelAndView);
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize(ROLE_ADMIN)
    public ModelAndView editProduct(@PathVariable String id, ModelAndView modelAndView) {
        ProductServiceModel productServiceModel = this.iProductService.findProductById(id);
        ProductAddBindingModel model = this.modelMapper.map(productServiceModel, ProductAddBindingModel.class);

        model.setCategories(productServiceModel.getCategories()
                .stream()
                .map(BaseServiceModel::getId)
                .collect(toList()));

        modelAndView.addObject("product", model);
        modelAndView.addObject("productId", id);

        return super.view(PRODUCT_EDIT, modelAndView);
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize(ROLE_ADMIN)
    public ModelAndView editProductConfirm(@PathVariable String id, @ModelAttribute ProductEditBindingModel productEditBindingModel) {
        this.iProductService.editProduct(
                id,
                this.modelMapper.map(productEditBindingModel, ProductServiceModel.class),
                productEditBindingModel.getCategories()
        );

        return super.redirect("/products/details/" + id);
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize(ROLE_ADMIN)
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

        return super.view(PRODUCT_DELETE, modelAndView);
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize(ROLE_ADMIN)
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

    @GetMapping("/top-offers")
    public ModelAndView topOffers(ModelAndView modelAndView){
        return super.view(PRODUCT_TOP_OFFERS, modelAndView);
    }

    @GetMapping("/fetch/top-offers")
    @ResponseBody
    public List<DiscountViewModel> findTopOffers(ModelAndView modelAndView) {

        List<DiscountServiceModel> discounts = this.iDiscountService.findAllDiscounts();

        return findAllDiscountsDistinctByProduct(discounts);
    }

    private List<DiscountViewModel> findAllDiscountsDistinctByProduct(List<DiscountServiceModel> discounts) {
        List<DiscountViewModel> topOffers = new ArrayList<>();
        boolean updated = false;

        for (DiscountServiceModel discount : discounts) {
            for (DiscountViewModel topOffer : topOffers) {
                if (topOffer.getProduct().getId().equals(discount.getProduct().getId())) {
                    int comparator = topOffer.getPrice().compareTo(discount.getPrice());

                    if(comparator == 0){
                        topOffer.setCreator(topOffer.getCreator() + " and " + discount.getCreator());
                    }else if (comparator > 0){
                        topOffer.setCreator(discount.getCreator());
                        topOffer.setPrice(discount.getPrice());
                    }

                    updated = true;
                    break;
                }
            }
            if(!updated){
                topOffers.add(this.modelMapper.map(discount, DiscountViewModel.class));
            }
            updated = false;
        }

        return topOffers;
    }

}
