package com.ivan.salesapp.web.controllers;

import com.ivan.salesapp.domain.models.binding.DiscountAddBindingModel;
import com.ivan.salesapp.domain.models.binding.DiscountEditBindingModel;
import com.ivan.salesapp.domain.models.binding.ProductDiscountBindingModel;
import com.ivan.salesapp.domain.models.service.DiscountServiceModel;
import com.ivan.salesapp.domain.models.service.ProductServiceModel;
import com.ivan.salesapp.domain.models.view.CategoryViewModel;
import com.ivan.salesapp.domain.models.view.DiscountViewModel;
import com.ivan.salesapp.domain.models.view.ProductAllViewModel;
import com.ivan.salesapp.services.IDiscountService;
import com.ivan.salesapp.services.IProductService;
import com.ivan.salesapp.services.IUserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/discounts")
public class DiscountController extends BaseController {
    private final IDiscountService iDiscountService;
    private final IProductService iProductService;
    private final IUserService iUserService;
    private final ModelMapper modelMapper;

    @Autowired
    public DiscountController(IDiscountService iDiscountService, IProductService iProductService, IUserService iUserService, ModelMapper modelMapper) {
        this.iDiscountService = iDiscountService;
        this.iProductService = iProductService;
        this.iUserService = iUserService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('ROLE_RESELLER')")
    public ModelAndView myDiscounts(Principal principal, ModelAndView modelAndView) {
        modelAndView.addObject("discounts", this.iDiscountService.findAllDiscountsByCreatorUsername(principal.getName())
                .stream()
                .map(d -> this.modelMapper.map(d, DiscountViewModel.class))
                .collect(toList()));

        return super.view("discount/my-discounts", modelAndView);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView allDiscounts(ModelAndView modelAndView) {
        modelAndView.addObject("discounts", this.iDiscountService.findAllDiscounts()
                .stream()
                .map(d -> this.modelMapper.map(d, DiscountViewModel.class))
                .collect(toList()));

        return super.view("discount/all-discounts", modelAndView);
    }

    @GetMapping("/choose-product")
    @PreAuthorize("hasRole('ROLE_RESELLER')")
    public ModelAndView chooseDiscountProduct(ModelAndView modelAndView) {
        modelAndView.addObject("products", this.iProductService.findAllProducts()
                .stream()
                .map(p -> this.modelMapper.map(p, ProductAllViewModel.class))
                .collect(toList()));

        return super.view("discount/choose-product", modelAndView);
    }

    @GetMapping("/product{id}")
    @PreAuthorize("hasRole('ROLE_RESELLER')")
    public ModelAndView discountChosenProduct(@PathVariable String id, ModelAndView modelAndView) {
        modelAndView.addObject("product", this.iProductService.findProductById(id));

        return super.view("discount/add-discount", modelAndView);
    }

    @PostMapping("/add{id}")
    @PreAuthorize("hasRole('ROLE_RESELLER')")
    public ModelAndView addDiscountConfirm(@PathVariable String id, @ModelAttribute DiscountAddBindingModel model, Principal principal) {
        this.iDiscountService.discountProduct(id, model, iUserService.findUserByUsername(principal.getName()));
        return super.redirect("/discounts/my");
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ROLE_RESELLER')")
    public ModelAndView editDiscount(@PathVariable String id, ModelAndView modelAndView) {
        DiscountServiceModel discountServiceModel = this.iDiscountService.findDiscountById(id);
        DiscountEditBindingModel model = this.modelMapper.map(discountServiceModel, DiscountEditBindingModel.class);

        modelAndView.addObject("discount", model);

        return super.view("discount/edit-discount", modelAndView);
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ROLE_RESELLER')")
    public ModelAndView editDiscountConfirm(@PathVariable String id, @ModelAttribute DiscountEditBindingModel model) {
        this.iDiscountService.editProductDiscount(this.modelMapper
                .map(model, DiscountServiceModel.class));

        return super.redirect("/discounts/all");
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_RESELLER')")
    public ModelAndView deleteDiscount(@PathVariable String id, ModelAndView modelAndView) {
        DiscountServiceModel discountServiceModel = this.iDiscountService.findDiscountById(id);
        DiscountEditBindingModel model = this.modelMapper.map(discountServiceModel, DiscountEditBindingModel.class);

        modelAndView.addObject("discount", model);

        return super.view("discount/delete-discount", modelAndView);
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_RESELLER')")
    public ModelAndView deleteDiscountConfirm(@PathVariable String id) {
        this.iDiscountService.deleteDiscount(id);

        return super.redirect("/discounts/my");
    }

    @GetMapping("/{category}")
    @ResponseBody
    public List<DiscountViewModel> fetchByCategory(@PathVariable String category) {
        return this.iDiscountService.findAllDiscounts()
                .stream()
                .map(o -> this.modelMapper.map(o, DiscountViewModel.class))
                .collect(toList());
    }

    @GetMapping("/fetch/{id}")
    @ResponseBody
    public List<DiscountViewModel> fetchDiscountsByProduct(@PathVariable String id) {
        return this.iDiscountService.findAllDiscountsByProductId(id)
                .stream()
                .map(d -> this.modelMapper.map(d, DiscountViewModel.class))
                .collect(toList());
    }
}