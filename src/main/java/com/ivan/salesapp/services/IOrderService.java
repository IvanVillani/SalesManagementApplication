package com.ivan.salesapp.services;


import com.ivan.salesapp.domain.models.service.OrderServiceModel;

import java.util.List;

public interface IOrderService {

    void createOrder(OrderServiceModel orderServiceModel);

    List<OrderServiceModel> findAllOrders();

    List<OrderServiceModel> findOrdersByCustomer(String username);

    OrderServiceModel findOrderById(String id);
}