package com.ivan.salesapp.repository;


import com.ivan.salesapp.domain.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findAllByCustomerOrderByRegisterDate(String customer);
}