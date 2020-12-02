package com.ivan.salesapp.domain.models.service;

import java.math.BigDecimal;

public class DiscountServiceModel extends BaseServiceModel {

    private ProductServiceModel product;
    private BigDecimal price;

    public DiscountServiceModel() {
    }

    public ProductServiceModel getProduct() {
        return product;
    }

    public void setProduct(ProductServiceModel product) {
        this.product = product;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
