package com.ivan.salesapp.web.controllers;

import com.ivan.salesapp.domain.models.rest.ProductOrderRequestModel;
import com.ivan.salesapp.services.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/order")
public class OrdersApiController {
    private final IOrderService iOrderService;

    @Autowired
    public OrdersApiController(IOrderService iOrderService) {
        this.iOrderService = iOrderService;
    }

    @PostMapping("/submit")
    public void submitOrder(@RequestBody ProductOrderRequestModel model, Principal principal) throws Exception {
        String name = principal.getName();
//        orderService.createOrder(model.getId(), name);
    }
}