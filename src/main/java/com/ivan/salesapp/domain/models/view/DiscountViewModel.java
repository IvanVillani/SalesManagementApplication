package com.ivan.salesapp.domain.models.view;

import java.math.BigDecimal;

public class DiscountViewModel {

    private String id;
    private ProductDetailsViewModel product;
    private BigDecimal price;

    public DiscountViewModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ProductDetailsViewModel getProduct() {
        return product;
    }

    public void setProduct(ProductDetailsViewModel product) {
        this.product = product;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
