package com.ivan.salesapp.domain.models.view;

import com.ivan.salesapp.domain.models.service.DiscountServiceModel;

public class OfferViewModel {
    private String id;
    private DiscountServiceModel discount;
    private Integer quantity;

    public OfferViewModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DiscountServiceModel getDiscount() {
        return discount;
    }

    public void setDiscount(DiscountServiceModel discount) {
        this.discount = discount;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
