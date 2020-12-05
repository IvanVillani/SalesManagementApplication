package com.ivan.salesapp.domain.models.view;

import java.math.BigDecimal;
import java.util.List;

public class DiscountProductViewModel {

    private ProductDetailsViewModel product;
    private List<DiscountViewModel> discounts;
    private BigDecimal price;

    public DiscountProductViewModel() {
    }

    public ProductDetailsViewModel getProduct() {
        return product;
    }

    public void setProduct(ProductDetailsViewModel product) {
        this.product = product;
    }

    public List<DiscountViewModel> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<DiscountViewModel> discounts) {
        this.discounts = discounts;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}