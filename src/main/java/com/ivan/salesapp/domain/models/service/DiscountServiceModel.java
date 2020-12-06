package com.ivan.salesapp.domain.models.service;

import java.math.BigDecimal;

public class DiscountServiceModel extends BaseServiceModel {
    private String creator;
    private ProductServiceModel product;
    private BigDecimal price;
    private Integer quantity;

    public DiscountServiceModel() {
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
