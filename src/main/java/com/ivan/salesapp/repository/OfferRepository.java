package com.ivan.salesapp.repository;

import com.ivan.salesapp.domain.entities.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface OfferRepository extends JpaRepository<Offer, String> {
    Offer findAllByNameAndCreatorAndDiscountPrice(String name, String creator, BigDecimal price);
}
