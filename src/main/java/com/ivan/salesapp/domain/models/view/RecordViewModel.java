package com.ivan.salesapp.domain.models.view;

import com.ivan.salesapp.domain.models.service.DiscountServiceModel;
import com.ivan.salesapp.domain.models.service.OrderServiceModel;
import com.ivan.salesapp.domain.models.service.ProductServiceModel;

import java.math.BigDecimal;
import java.util.List;

public class RecordViewModel {
    private String id;
    private OrderViewModel order;
    private ProductDetailsViewModel product;
    private List<OfferViewModel> offers;
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

    public ProductDetailsViewModel getProduct() {
        return product;
    }

    public void setProduct(ProductDetailsViewModel product) {
        this.product = product;
    }

    public List<OfferViewModel> getOffers() {
        return offers;
    }

    public void setOffers(List<OfferViewModel> offers) {
        this.offers = offers;
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

    public int getFullQuantity() {
        return fullQuantity;
    }

    public void setFullQuantity(int fullQuantity) {
        this.fullQuantity = fullQuantity;
    }
}
