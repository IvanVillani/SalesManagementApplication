package com.ivan.salesapp.domain.models.view;

import com.ivan.salesapp.domain.models.service.DiscountServiceModel;
import com.ivan.salesapp.domain.models.service.OrderProductServiceModel;
import com.ivan.salesapp.domain.models.service.OrderServiceModel;
import com.ivan.salesapp.domain.models.service.ProductServiceModel;

import java.math.BigDecimal;
import java.util.List;

public class RecordViewModel {
    private String id;
    private OrderViewModel order;
    private OrderProductServiceModel product;
    private BigDecimal price;
    private Integer discountQuantity;
    private Integer stockQuantity;
    private Integer fullQuantity;

    public RecordViewModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OrderViewModel getOrder() {
        return order;
    }

    public void setOrder(OrderViewModel order) {
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
