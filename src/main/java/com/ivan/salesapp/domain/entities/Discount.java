package com.ivan.salesapp.domain.entities;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "discounts")
public class Discount extends BaseEntity {

    private Product product;
    private BigDecimal price;

    public Discount() {
    }

    @ManyToOne(targetEntity = Product.class)
    @JoinColumn(
            name = "product_id",
            referencedColumnName = "id"
    )
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Column(name = "price")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
