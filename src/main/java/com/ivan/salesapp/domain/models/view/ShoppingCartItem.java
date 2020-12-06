package com.ivan.salesapp.domain.models.view;

import java.io.Serializable;

public class ShoppingCartItem implements Serializable {

    private DiscountProductViewModel product;
    private int quantity;
    private int stockQuantity;

    public ShoppingCartItem() {
    }

    public DiscountProductViewModel getProduct() {
        return product;
    }

    public void setProduct(DiscountProductViewModel product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}