package com.ivan.salesapp.services;


import com.ivan.salesapp.domain.entities.Order;
import com.ivan.salesapp.domain.models.service.OrderServiceModel;
import com.ivan.salesapp.domain.models.service.RecordServiceModel;
import com.ivan.salesapp.exceptions.OrderNotFoundException;
import com.ivan.salesapp.exceptions.ProductNotFoundException;

import java.util.List;

public interface IOrderService {

    void createOrder(OrderServiceModel orderServiceModel, List<RecordServiceModel> models);

    List<OrderServiceModel> findAllOrders();

    List<OrderServiceModel> findOrdersByCustomer(String username);

    List<OrderServiceModel> findOrdersInRange(String start, String end);

    OrderServiceModel findOrderById(String id) throws OrderNotFoundException;

    void updateProductsStock(List<RecordServiceModel> records) throws ProductNotFoundException;

    void checkStockAndNotifyByMail(List<RecordServiceModel> records) throws ProductNotFoundException;
}