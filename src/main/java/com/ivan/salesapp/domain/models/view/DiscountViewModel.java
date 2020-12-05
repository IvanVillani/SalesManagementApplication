package com.ivan.salesapp.domain.models.view;

import java.math.BigDecimal;

public class DiscountViewModel {

    private String id;
    private String creator;
    private ProductDetailsViewModel product;
    private BigDecimal price;
    private Integer quantity;

    public DiscountViewModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
