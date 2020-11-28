package com.ivan.salesapp.domain.models.binding;

import com.ivan.salesapp.domain.entities.Category;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

public class ProductAddBindingModel {
    private String category;
    private String product;
    private long quantity;
    private BigDecimal price;

    public ProductAddBindingModel() {
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
