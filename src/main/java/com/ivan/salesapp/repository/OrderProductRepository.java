package com.ivan.salesapp.repository;

import com.ivan.salesapp.domain.entities.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, String> {
}
