package com.ivan.salesapp.domain.models.view;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderViewModel {

    private String id;
    private List<DiscountProductViewModel> products;
    private UserProfileViewModel customer;
    private BigDecimal totalPrice;
    private LocalDateTime registerDate;

    public OrderViewModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<DiscountProductViewModel> getProducts() {
        return products;
    }

    public void setProducts(List<DiscountProductViewModel> products) {
        this.products = products;
    }

    public UserProfileViewModel getCustomer() {
        return customer;
    }

    public void setCustomer(UserProfileViewModel customer) {
        this.customer = customer;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(LocalDateTime registerDate) {
        this.registerDate = registerDate;
    }
}