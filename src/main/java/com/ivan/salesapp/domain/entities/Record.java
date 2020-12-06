package com.ivan.salesapp.domain.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "records")
public class Record extends BaseEntity {

    private Order order;
    private Product product;
    private List<Offer> offers;
    private BigDecimal price;
    private Integer discountQuantity;
    private Integer stockQuantity;
    private Integer fullQuantity;

    public Record() {
    }

    @OneToOne(targetEntity = Order.class)
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @OneToOne(targetEntity = Product.class)
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @OneToMany(targetEntity = Offer.class, cascade=CascadeType.ALL)
    @JoinColumn(
            name = "offer_id",
            referencedColumnName = "id"
    )
    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    @Column(name = "price")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Column(name = "discountQuantity")
    public Integer getDiscountQuantity() {
        return discountQuantity;
    }

    public void setDiscountQuantity(Integer discountQuantity) {
        this.discountQuantity = discountQuantity;
    }

    @Column(name = "stockQuantity")
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
