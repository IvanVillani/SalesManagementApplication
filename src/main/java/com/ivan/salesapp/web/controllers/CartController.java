package com.ivan.salesapp.web.controllers;

import com.ivan.salesapp.constants.ExceptionMessageConstants;
import com.ivan.salesapp.constants.RoleConstants;
import com.ivan.salesapp.constants.ViewConstants;
import com.ivan.salesapp.domain.entities.Order;
import com.ivan.salesapp.domain.models.service.*;
import com.ivan.salesapp.domain.models.view.DiscountProductViewModel;
import com.ivan.salesapp.domain.models.view.DiscountViewModel;
import com.ivan.salesapp.domain.models.view.ProductDetailsViewModel;
import com.ivan.salesapp.domain.models.view.ShoppingCartItem;
import com.ivan.salesapp.services.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;

import static com.ivan.salesapp.constants.RoleConstants.ROLE_CLIENT;
import static java.util.stream.Collectors.toList;

@Controller
@RequestMapping("/cart")
@PreAuthorize(ROLE_CLIENT)
public class CartController extends BaseController implements RoleConstants, ViewConstants, ExceptionMessageConstants {
    private final IDiscountService iDiscountService;
    private final IProductService iProductService;
    private final IUserService iUserService;
    private final IOrderService iOrderService;
    private final ModelMapper modelMapper;

    @Autowired
    public CartController(IDiscountService iDiscountService, IProductService iProductService, IUserService iUserService, IOrderService iOrderService, ModelMapper modelMapper) {
        this.iDiscountService = iDiscountService;
        this.iProductService = iProductService;
        this.iUserService = iUserService;
        this.iOrderService = iOrderService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/add-product")
    @PreAuthorize(ROLE_CLIENT)
    public ModelAndView addProductToCart(String productId, String discountId, int quantity, HttpSession session) {
        ProductDetailsViewModel product = this.modelMapper
                .map(this.iProductService.findProductById(productId), ProductDetailsViewModel.class);

        DiscountProductViewModel discountProductViewModel = new DiscountProductViewModel();
        discountProductViewModel.setProduct(product);
        discountProductViewModel.setDiscounts(new LinkedList<>());

        ShoppingCartItem cartItem = new ShoppingCartItem();

        if(!discountId.equals(productId)){
            discountProductViewModel.getDiscounts()
                    .add(this.modelMapper
                            .map(this.iDiscountService.findDiscountById(discountId), DiscountViewModel.class));
            discountProductViewModel.getDiscounts().get(0).setQuantity(quantity);
        }else{
            cartItem.setStockQuantity(quantity);
        }

        cartItem.setProduct(discountProductViewModel);
        cartItem.setQuantity(quantity);

        List<ShoppingCartItem> cartItems = this.retrieveCart(session);
        this.addItemToCart(cartItem, cartItems);

        return super.redirect("/home");
    }

    @GetMapping("/details")
    @PreAuthorize(ROLE_CLIENT)
    public ModelAndView cartDetails(ModelAndView modelAndView, HttpSession session) {
        List<ShoppingCartItem> cartItems = this.retrieveCart(session);
        modelAndView.addObject("totalPrice", this.calcTotal(cartItems));
        modelAndView.addObject("discounts", cartItems.stream().map(c -> c.getProduct().getDiscounts()));

        return super.view(CART_DETAILS, modelAndView);
    }

    @DeleteMapping("/remove-product")
    @PreAuthorize(ROLE_CLIENT)
    public ModelAndView removeFromCartConfirm(String id, HttpSession session) {
        this.removeItemFromCart(id, this.retrieveCart(session));

        return super.redirect("/cart/details");
    }


    @PostMapping("/checkout")
    @PreAuthorize(ROLE_CLIENT)
    public ModelAndView checkoutConfirm(HttpSession session, Principal principal) {
        List<ShoppingCartItem> cartItems = this.retrieveCart(session);

        List<RecordServiceModel> models = extractDataIntoRecordServiceModel(cartItems);

        OrderServiceModel orderServiceModel = this.prepareOrder(cartItems, principal.getName());
        this.iOrderService.createOrder(orderServiceModel, models);

        this.iOrderService.updateProductsStock(models);

        this.iOrderService.checkStockAndNotifyByMail(models);

        cartItems.clear();

        return super.redirect("/home");
    }

    private List<ShoppingCartItem> retrieveCart(HttpSession session) {
        this.initCart(session);

        return (List<ShoppingCartItem>) session.getAttribute("shopping-cart");
    }

    private void initCart(HttpSession session) {
        if (session.getAttribute("shopping-cart") == null) {
            session.setAttribute("shopping-cart", new LinkedList<>());
        }
    }

    private void addItemToCart(ShoppingCartItem item, List<ShoppingCartItem> cartItems) {
        if (itemWithSameProductInCart(cartItems, item)){
            ShoppingCartItem shoppingCartItem = findItemWithSameProduct(cartItems ,item);

            mergeItemsWithSameProduct(shoppingCartItem, item);
            return;
        }
        cartItems.add(item);
    }

    private boolean itemWithSameProductInCart(List<ShoppingCartItem> cartItems, ShoppingCartItem item){
        for (ShoppingCartItem shoppingCartItem : cartItems) {
            if (shoppingCartItem.getProduct().getProduct().getId().equals(item.getProduct().getProduct().getId())) {
                return true;
            }
        }

        return false;
    }

    private ShoppingCartItem findItemWithSameProduct(List<ShoppingCartItem> cartItems, ShoppingCartItem item){
        return cartItems
                .stream()
                .filter(shoppingCartItem ->
                        shoppingCartItem.getProduct().getProduct().getId().equals(item.getProduct().getProduct().getId()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(ITEM_WITH_SAME_PRODUCT));
    }

    private void mergeItemsWithSameProduct(ShoppingCartItem shoppingCartItem, ShoppingCartItem newItem){
        if (!newItem.getProduct().getDiscounts().isEmpty()){
            if(shoppingCartItem.getProduct().getDiscounts().isEmpty()){
                shoppingCartItem.getProduct().setDiscounts(new LinkedList<>());
            }else{
                for (DiscountViewModel discount : shoppingCartItem.getProduct().getDiscounts()) {
                    if(discount.getCreator().equals(newItem.getProduct().getDiscounts().get(0).getCreator())){
                        discount.setQuantity(discount.getQuantity() + newItem.getQuantity());
                        shoppingCartItem.setQuantity(shoppingCartItem.getQuantity() + newItem.getQuantity());
                        return;
                    }
                }
            }
            shoppingCartItem.getProduct().getDiscounts().addAll(newItem.getProduct().getDiscounts());
        }
        shoppingCartItem.setQuantity(shoppingCartItem.getQuantity() + newItem.getQuantity());
        shoppingCartItem.setStockQuantity(shoppingCartItem.getStockQuantity() + newItem.getStockQuantity());
    }

    private void removeItemFromCart(String id, List<ShoppingCartItem> cartItems) {
        cartItems.removeIf(item -> item.getProduct().getProduct().getId().equals(id));
    }

    private BigDecimal calcTotal(List<ShoppingCartItem> cartItems) {
        List<ShoppingCartItem> updatedCartItems = updateQuantities(cartItems);

        BigDecimal totalPrice = calculateTotalPrice(updatedCartItems);

        BigDecimal result = new BigDecimal(0);

        for (ShoppingCartItem item : cartItems) {
            BigDecimal productPrice = new BigDecimal(0);

            int fullQuantity = item.getQuantity();
            int stockQuantity = 0;
            int discountsQuantity = 0;

            if (!item.getProduct().getDiscounts().isEmpty()){
                for (DiscountViewModel discount : item.getProduct().getDiscounts()) {
                    discountsQuantity += discount.getQuantity();
                    result = result.add(discount.getPrice().multiply(new BigDecimal(discount.getQuantity())));
                    productPrice = productPrice.add(discount.getPrice().multiply(new BigDecimal(discount.getQuantity())));
                }
            }
            stockQuantity = fullQuantity - discountsQuantity;

            result = result.add(item.getProduct().getProduct().getPrice().multiply(new BigDecimal(stockQuantity)));

            productPrice = productPrice.add(item.getProduct().getProduct().getPrice().multiply(new BigDecimal(stockQuantity)));

            item.getProduct().setPrice(productPrice);
        }

        return result;
    }

    private List<ShoppingCartItem> updateQuantities(List<ShoppingCartItem> cartItems){
        for (ShoppingCartItem item : cartItems) {
            BigDecimal productPrice = new BigDecimal(0);

            int fullQuantity = item.getQuantity();
            int stockQuantity = 0;
            int discountsQuantity = 0;

            if (!item.getProduct().getDiscounts().isEmpty()){
                for (DiscountViewModel discount : item.getProduct().getDiscounts()) {
                    discountsQuantity += discount.getQuantity();
                    productPrice = productPrice.add(discount.getPrice().multiply(new BigDecimal(discount.getQuantity())));
                }
            }
            stockQuantity = fullQuantity - discountsQuantity;

            productPrice = productPrice.add(item.getProduct().getProduct().getPrice().multiply(new BigDecimal(stockQuantity)));

            item.getProduct().setPrice(productPrice);
        }

        return cartItems;
    }

    private BigDecimal calculateTotalPrice(List<ShoppingCartItem> updatedCartItems){
        BigDecimal result = new BigDecimal(0);

        for (ShoppingCartItem item : updatedCartItems) {
            if (!item.getProduct().getDiscounts().isEmpty()){
                for (DiscountViewModel discount : item.getProduct().getDiscounts()) {
                    result = result.add(discount.getPrice().multiply(new BigDecimal(discount.getQuantity())));
                }
            }
            result = result.add(item.getProduct().getProduct().getPrice().multiply(new BigDecimal(item.getStockQuantity())));
        }

        return result;
    }

    private OrderServiceModel prepareOrder(List<ShoppingCartItem> cartItems, String customer) {
        OrderServiceModel orderServiceModel = new OrderServiceModel();

        orderServiceModel.setCustomer(this.iUserService.findUserByUsername(customer));

        List<OrderProductServiceModel> products = new ArrayList<>();

        for (ShoppingCartItem item : cartItems) {
            OrderProductServiceModel productServiceModel = this.modelMapper.map(item.getProduct(), OrderProductServiceModel.class);

            for (int i = 0; i < item.getQuantity(); i++) {
                products.add(productServiceModel);
            }
        }

        orderServiceModel.setProducts(products);
        orderServiceModel.setTotalPrice(this.calcTotal(cartItems));

        return orderServiceModel;
    }

    private List<RecordServiceModel> extractDataIntoRecordServiceModel(List<ShoppingCartItem> cartItems){
        List<RecordServiceModel> models = new ArrayList<>();

        for (ShoppingCartItem shoppingCartItem : cartItems) {
            RecordServiceModel model = createRecordFromShoppingCartItem(shoppingCartItem);

            models.add(model);
        }

        return models;
    }

    private RecordServiceModel createRecordFromShoppingCartItem(ShoppingCartItem shoppingCartItem){
        RecordServiceModel model = this.modelMapper.map(shoppingCartItem, RecordServiceModel.class);

        model.setFullQuantity(shoppingCartItem.getQuantity());
        model.setStockQuantity(shoppingCartItem.getStockQuantity());
        model.setDiscountQuantity(shoppingCartItem.getQuantity() - shoppingCartItem.getStockQuantity());

        List<DiscountViewModel> srcDiscounts = shoppingCartItem.getProduct().getDiscounts();
        model.setOffers(getOffersFromDiscounts(srcDiscounts));

        return model;
    }

    private List<OfferServiceModel> getOffersFromDiscounts(List<DiscountViewModel> srcDiscounts){
        List<OfferServiceModel> offers = new ArrayList<>();

        for (DiscountViewModel srcDiscount : srcDiscounts) {
            OfferServiceModel offer = new OfferServiceModel();
            offer.setDiscount(this.modelMapper.map(srcDiscount, DiscountServiceModel.class));
            offer.setQuantity(srcDiscount.getQuantity());
            offers.add(offer);
        }
        return offers;
    }


}