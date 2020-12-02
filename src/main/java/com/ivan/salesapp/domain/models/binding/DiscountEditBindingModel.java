package com.ivan.salesapp.domain.models.binding;

import com.ivan.salesapp.domain.models.service.ProductServiceModel;

import java.math.BigDecimal;

public class DiscountEditBindingModel {
    private String id;
    private ProductServiceModel product;
    private BigDecimal price;

    public DiscountEditBindingModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
