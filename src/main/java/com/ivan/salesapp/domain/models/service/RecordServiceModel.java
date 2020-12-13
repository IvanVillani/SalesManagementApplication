package com.ivan.salesapp.domain.models.service;

import java.math.BigDecimal;
import java.util.List;

public class RecordServiceModel extends BaseServiceModel{
    private OrderServiceModel order;
    private OrderProductServiceModel product;
    private BigDecimal price;
    private Integer discountQuantity;
    private Integer stockQuantity;
    private Integer fullQuantity;

    public RecordServiceModel() {
    }

    public OrderServiceModel getOrder() {
        return order;
    }

    public void setOrder(OrderServiceModel order) {
        this.order = order;
    }

    public OrderProductServiceModel getProduct() {
        return product;
    }

    public void setProduct(OrderProductServiceModel product) {
        this.product = product;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getDiscountQuantity() {
        return discountQuantity;
    }

    public void setDiscountQuantity(Integer discountQuantity) {
        this.discountQuantity = discountQuantity;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Integer getFullQuantity() {
        return fullQuantity;
    }

    public void setFullQuantity(Integer fullQuantity) {
        this.fullQuantity = fullQuantity;
    }
}
