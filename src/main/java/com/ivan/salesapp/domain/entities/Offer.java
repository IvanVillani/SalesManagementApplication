package com.ivan.salesapp.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "offers")
public class Offer extends BaseEntity{
    private Discount discount;
    private Integer quantity;

    public Offer() {
    }

    @OneToOne(targetEntity = Discount.class)
    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    @Column(name = "quantity")
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
