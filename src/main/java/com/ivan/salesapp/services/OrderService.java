package com.ivan.salesapp.services;


import com.ivan.salesapp.domain.entities.Order;
import com.ivan.salesapp.domain.entities.Product;
import com.ivan.salesapp.domain.models.service.OrderServiceModel;
import com.ivan.salesapp.domain.models.service.RecordServiceModel;
import com.ivan.salesapp.repository.OrderRepository;
import com.ivan.salesapp.repository.ProductRepository;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class OrderService implements IOrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final IRecordService iRecordService;
    private final IEmailService iEmailService;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            IRecordService iRecordService, IEmailService iEmailService, ModelMapper modelMapper
    ) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.iRecordService = iRecordService;
        this.iEmailService = iEmailService;
        this.modelMapper = modelMapper;
    }


    @Async
    @Override
    @Transactional
    public void createOrder(OrderServiceModel orderServiceModel, List<RecordServiceModel> models) {
        orderServiceModel.setRegisterDate(LocalDateTime.now());

        Order order = this.modelMapper.map(orderServiceModel, Order.class);

        updateProductsStockAndNotifyByMail(models);

        this.orderRepository.saveAndFlush(order);

        List<OrderServiceModel> orders = this.orderRepository.findAll()
                .stream()
                .map(o -> this.modelMapper.map(o, OrderServiceModel.class))
                .filter(o -> o.getRegisterDate().isEqual(orderServiceModel.getRegisterDate()))
                .collect(toList());

        if (!orders.isEmpty()){
            for (RecordServiceModel model : models) {
                model.setOrder(orders.get(0));
                this.iRecordService.createRecord(model);
            }
        }

    }

    @Override
    public List<OrderServiceModel> findAllOrders() {
        List<Order> orders = this.orderRepository.findAll();

        return orders
                .stream()
                .sorted(Comparator.comparing(Order::getRegisterDate, Comparator.reverseOrder()))
                .map(o -> this.modelMapper.map(o, OrderServiceModel.class))
                .collect(toList());
    }

    @Override
    public List<OrderServiceModel> findOrdersByCustomer(String username) {
        return this.orderRepository.findAllOrdersByCustomerUsernameOrderByRegisterDate(username)
                .stream()
                .map(o -> modelMapper.map(o, OrderServiceModel.class))
                .collect(toList());
    }

    @Override
    public List<OrderServiceModel> findOrdersInRange(String start, String end) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        LocalDateTime startDateTime = LocalDateTime.parse(start + " 00:00", dtf);
        LocalDateTime endDateTime = LocalDateTime.parse(end + " 23:59", dtf);

        return this.orderRepository.findAll()
                .stream()
                .filter(o -> startDateTime.isBefore(o.getRegisterDate()) &&
                        endDateTime.isAfter(o.getRegisterDate()))
                .sorted(Comparator.comparing(Order::getRegisterDate, Comparator.reverseOrder()))
                .map(o -> this.modelMapper.map(o, OrderServiceModel.class))
                .collect(toList());
    }

    @Override
    public OrderServiceModel findOrderById(String id) {
        return this.orderRepository.findById(id)
                .map(o -> this.modelMapper.map(o, OrderServiceModel.class))
                .orElseThrow(() ->
                        new IllegalArgumentException(String.format("No such order with id:%s!", id)));
    }

    @Async
    @Override
    public void updateProductsStockAndNotifyByMail(List<RecordServiceModel> records){
        for (RecordServiceModel record : records) {
            Product product = this.productRepository.findById(record.getProduct().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Product to update not found!"));

            product.setStock(product.getStock() - record.getFullQuantity());

            if (product.getStock() <= 10){
                String subject = String.format("WARNING:%S quantity is critical!", product.getName());
                String text = String.format("Dear Administrator,\n\nThis email was sent to inform you " +
                        "that the quantity of %s with product-ID:%s\n" +
                        "is low and below the minimum of 10 in stock.\n We recommend you to restock.\n" + "--Details: \n" +
                        "---Last order proceeded: \n---Customer: \n" + "---Ordered: %s\n" + "---Remaining: %s\n\n" +
                        "Please do not reply to this email! It was sent by the system of Home Design Store.\n" +
                        "Thank you!\n\n" +
                        "Best Regards,\n" +
                        "Team Home Design Store", product.getName(), product.getId(), record.getFullQuantity(), product.getStock());
                this.iEmailService.sendSimpleMessage("navisdays@gmail.com", subject, text);
            }
        }
    }
}