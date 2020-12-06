package com.ivan.salesapp.domain.models.binding;

import java.math.BigDecimal;

public class DiscountAddBindingModel {
    private String id;
    private BigDecimal price;

    public DiscountAddBindingModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
