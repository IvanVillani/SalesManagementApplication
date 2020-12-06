package com.ivan.salesapp.domain.models.service;

public class OfferServiceModel extends BaseServiceModel{
    private DiscountServiceModel discount;
    private Integer quantity;

    public OfferServiceModel() {
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
