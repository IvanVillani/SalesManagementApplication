package com.ivan.salesapp.domain.models.service;

import java.math.BigDecimal;
import java.util.List;

public class OrderProductServiceModel extends BaseServiceModel {

    private String name;
    private String description;
    private BigDecimal price;
    private long stock;
    private String imageUrl;
    private List<OfferServiceModel> offers;
    private BigDecimal orderPrice;

    public OrderProductServiceModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public long getStock() {
        return stock;
    }

    public void setStock(long stock) {
        this.stock = stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<OfferServiceModel> getOffers() {
        return offers;
    }

    public void setOffers(List<OfferServiceModel> offers) {
        this.offers = offers;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }
}