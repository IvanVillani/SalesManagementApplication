package com.ivan.salesapp.services;


import com.ivan.salesapp.constants.ExceptionMessageConstants;
import com.ivan.salesapp.constants.MailSenderConstants;
import com.ivan.salesapp.domain.entities.Order;
import com.ivan.salesapp.domain.entities.Product;
import com.ivan.salesapp.domain.models.service.OrderServiceModel;
import com.ivan.salesapp.domain.models.service.RecordServiceModel;
import com.ivan.salesapp.domain.models.service.RoleServiceModel;
import com.ivan.salesapp.domain.models.service.UserServiceModel;
import com.ivan.salesapp.enums.UserRole;
import com.ivan.salesapp.exceptions.OrderNotFoundException;
import com.ivan.salesapp.exceptions.ProductNotFoundException;
import com.ivan.salesapp.repository.OrderRepository;
import com.ivan.salesapp.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class OrderService implements IOrderService, MailSenderConstants, ExceptionMessageConstants {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final IUserService iUserService;
    private final IRecordService iRecordService;
    private final IEmailService iEmailService;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            IUserService iUserService, IRecordService iRecordService, IEmailService iEmailService, ModelMapper modelMapper
    ) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.iUserService = iUserService;
        this.iRecordService = iRecordService;
        this.iEmailService = iEmailService;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public void createOrder(OrderServiceModel orderServiceModel, List<RecordServiceModel> models) {
        orderServiceModel.setRegisterDate(LocalDateTime.now());

        Order order = this.modelMapper.map(orderServiceModel, Order.class);

        this.orderRepository.saveAndFlush(order);

        List<OrderServiceModel> orders = findOrdersByRegisterDate(orderServiceModel);

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
        List<OrderServiceModel> orders = this.orderRepository.findAllByCustomerOrderByRegisterDate(username)
                .stream()
                .map(o -> modelMapper.map(o, OrderServiceModel.class))
                .collect(toList());

        return orders;
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
    public OrderServiceModel findOrderById(String id) throws OrderNotFoundException {
        return this.orderRepository.findById(id)
                .map(o -> this.modelMapper.map(o, OrderServiceModel.class))
                .orElseThrow(() ->
                        new OrderNotFoundException(String.format(ORDER_NOT_FOUND, id)));
    }

    @Override
    public void updateProductsStock(List<RecordServiceModel> records) throws ProductNotFoundException {
        for (RecordServiceModel record : records) {
            Product product = this.productRepository.findById(record.getProduct().getId())
                    .orElseThrow(() -> new ProductNotFoundException(PRODUCT_TO_UPDATE_NOT_FOUND));

            product.setStock(product.getStock() - record.getFullQuantity());
            this.productRepository.saveAndFlush(product);
        }
    }

    @Async
    @Override
    public void checkStockAndNotifyByMail(List<RecordServiceModel> records) throws ProductNotFoundException {
        for (RecordServiceModel record : records) {
            Product product = this.productRepository.findById(record.getProduct().getId())
                    .orElseThrow(() -> new ProductNotFoundException(PRODUCT_TO_UPDATE_NOT_FOUND));

            if (product.getStock() <= 10){
                String subject = String.format(MailSenderConstants.MESSAGE_SUBJECT, product.getName());
                String text = String.format(MailSenderConstants.MESSAGE_TEXT, product.getName(), product.getId(), record.getFullQuantity(), product.getStock());

                String receiver = getAdminEmailAddress();

                this.iEmailService.sendSimpleMessage(receiver, subject, text);
            }
        }
    }

    private List<OrderServiceModel> findOrdersByRegisterDate(OrderServiceModel orderServiceModel) {
        return this.orderRepository.findAll()
                .stream()
                .map(o -> this.modelMapper.map(o, OrderServiceModel.class))
                .filter(o -> o.getRegisterDate().isEqual(orderServiceModel.getRegisterDate()))
                .collect(toList());
    }

    private String getAdminEmailAddress(){
        List<UserServiceModel> users = this.iUserService.findAllUsers();

        for (UserServiceModel user : users) {
            if(user.getAuthorities().size() == 1){
                String authority = new ArrayList<>(user.getAuthorities()).get(0).getAuthority();
                if(authority.equals(UserRole.ADMIN.toString())){
                    return user.getEmail();
                }
            }
        }

        return MailSenderConstants.USERNAME;
    }
}