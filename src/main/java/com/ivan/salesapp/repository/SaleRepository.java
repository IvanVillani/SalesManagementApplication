package com.ivan.salesapp.repository;

import com.ivan.salesapp.domain.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends JpaRepository<Order, String> {
}
