package com.ivan.salesapp.web.controllers;

import com.ivan.salesapp.constants.ExceptionMessageConstants;
import com.ivan.salesapp.constants.RoleConstants;
import com.ivan.salesapp.constants.ViewConstants;
import com.ivan.salesapp.domain.models.binding.DiscountAddBindingModel;
import com.ivan.salesapp.domain.models.binding.DiscountEditBindingModel;
import com.ivan.salesapp.domain.models.service.DiscountServiceModel;
import com.ivan.salesapp.domain.models.view.DiscountViewModel;
import com.ivan.salesapp.domain.models.view.ProductAllViewModel;
import com.ivan.salesapp.exceptions.DiscountNotFoundException;
import com.ivan.salesapp.exceptions.NoResellerDiscountsException;
import com.ivan.salesapp.exceptions.ProductNotFoundException;
import com.ivan.salesapp.services.IDiscountService;
import com.ivan.salesapp.services.IProductService;
import com.ivan.salesapp.services.ISocialService;
import com.ivan.salesapp.services.IUserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import twitter4j.TwitterException;

import java.security.Principal;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/discounts")
public class DiscountController extends BaseController implements RoleConstants, ViewConstants, ExceptionMessageConstants {
    private final IDiscountService iDiscountService;
    private final IProductService iProductService;
    private final IUserService iUserService;
    private final ISocialService iSocialService;
    private final ModelMapper modelMapper;

    @Autowired
    public DiscountController(IDiscountService iDiscountService, IProductService iProductService, IUserService iUserService, ISocialService iSocialService, ModelMapper modelMapper) {
        this.iDiscountService = iDiscountService;
        this.iProductService = iProductService;
        this.iUserService = iUserService;
        this.iSocialService = iSocialService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/my")
    @PreAuthorize(ROLE_RESELLER)
    public ModelAndView myDiscounts(Principal principal, ModelAndView modelAndView) {
        modelAndView.addObject("discounts", this.iDiscountService.findAllDiscountsByCreatorUsername(principal.getName())
                .stream()
                .map(d -> this.modelMapper.map(d, DiscountViewModel.class))
                .collect(toList()));

        return super.view(DISCOUNT_MY, modelAndView);
    }

    @GetMapping("/all")
    @PreAuthorize(ROLE_ADMIN)
    public ModelAndView allDiscounts(ModelAndView modelAndView) {
        modelAndView.addObject("discounts", this.iDiscountService.findAllDiscounts()
                .stream()
                .map(d -> this.modelMapper.map(d, DiscountViewModel.class))
                .collect(toList()));

        return super.view(DISCOUNT_ALL, modelAndView);
    }

    @PostMapping("/search")
    @PreAuthorize(ROLE_ADMIN)
    public ModelAndView allDiscountsForReseller(String reseller, ModelAndView modelAndView) throws NoResellerDiscountsException {
        List<DiscountViewModel> discounts = this.iDiscountService.findAllDiscounts()
                .stream()
                .filter(d -> reseller.equals(d.getCreator()))
                .map(d -> this.modelMapper.map(d, DiscountViewModel.class))
                .collect(toList());

        if(discounts.isEmpty()){
            throw new NoResellerDiscountsException(NO_RESELLER_DISCOUNTS, DISCOUNT_ALL);
        }else{
            modelAndView.addObject("discounts", discounts);
        }

        return super.view(DISCOUNT_ALL, modelAndView);
    }

    @GetMapping("/choose-product")
    @PreAuthorize(ROLE_RESELLER)
    public ModelAndView chooseDiscountProduct(ModelAndView modelAndView) {
        modelAndView.addObject("products", this.iProductService.findAllProducts()
                .stream()
                .map(p -> this.modelMapper.map(p, ProductAllViewModel.class))
                .collect(toList()));

        return super.view(DISCOUNT_CHOOSE_PRODUCT, modelAndView);
    }

    @GetMapping("/product{id}")
    @PreAuthorize(ROLE_RESELLER)
    public ModelAndView discountChosenProduct(@PathVariable String id, ModelAndView modelAndView) throws ProductNotFoundException {
        modelAndView.addObject("product", this.iProductService.findProductById(id));

        return super.view(DISCOUNT_ADD, modelAndView);
    }

    @PostMapping("/add{id}")
    @PreAuthorize(ROLE_RESELLER)
    public ModelAndView addDiscountConfirm(@PathVariable String id, @ModelAttribute DiscountAddBindingModel model, Principal principal, ModelAndView modelAndView) {
        DiscountServiceModel discount = this.iDiscountService
                .discountProduct(id, model, iUserService.findUserByUsername(principal.getName()));
        modelAndView.addObject("discount", discount);
        return super.view(DISCOUNT_TWEET, modelAndView);
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize(ROLE_RESELLER)
    public ModelAndView editDiscount(@PathVariable String id, ModelAndView modelAndView) throws DiscountNotFoundException {
        DiscountServiceModel discountServiceModel = this.iDiscountService.findDiscountById(id);
        DiscountEditBindingModel model = this.modelMapper.map(discountServiceModel, DiscountEditBindingModel.class);

        modelAndView.addObject("discount", model);
        modelAndView.addObject("productPrice", model.getProduct().getPrice());

        return super.view(DISCOUNT_EDIT, modelAndView);
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize(ROLE_RESELLER)
    public ModelAndView editDiscountConfirm(@PathVariable String id, @ModelAttribute DiscountEditBindingModel model) throws DiscountNotFoundException {
        this.iDiscountService.editProductDiscount(this.modelMapper
                .map(model, DiscountServiceModel.class));

        return super.redirect("/discounts/my");
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize(ROLE_RESELLER)
    public ModelAndView deleteDiscount(@PathVariable String id, ModelAndView modelAndView) throws DiscountNotFoundException {
        DiscountServiceModel discountServiceModel = this.iDiscountService.findDiscountById(id);
        DiscountEditBindingModel model = this.modelMapper.map(discountServiceModel, DiscountEditBindingModel.class);

        modelAndView.addObject("discount", model);

        return super.view(DISCOUNT_DELETE, modelAndView);
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize(ROLE_RESELLER)
    public ModelAndView deleteDiscountConfirm(@PathVariable String id) throws DiscountNotFoundException {
        this.iDiscountService.deleteDiscount(id);

        return super.redirect("/discounts/my");
    }

    @GetMapping("/tweet/{id}")
    @PreAuthorize(ROLE_RESELLER)
    public ModelAndView tweetDiscount(@PathVariable String id) {
        try {
            this.iSocialService.createTweet(this.iDiscountService.findDiscountById(id));
        } catch (TwitterException | DiscountNotFoundException e) {
            e.printStackTrace();
        }
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