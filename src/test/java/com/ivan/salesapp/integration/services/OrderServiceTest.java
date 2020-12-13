package com.ivan.salesapp.integration.services;

import com.ivan.salesapp.domain.entities.*;
import com.ivan.salesapp.domain.models.service.*;
import com.ivan.salesapp.exceptions.OrderNotFoundException;
import com.ivan.salesapp.repository.OrderRepository;
import com.ivan.salesapp.repository.ProductRepository;
import com.ivan.salesapp.services.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class OrderServiceTest {
    @Autowired
    IOrderService iOrderService;

    @MockBean
    OrderRepository mockOrderRepository;

    @MockBean
    ProductRepository mockProductRepository;

    @MockBean
    IRecordService mockIRecordService;

    @MockBean
    IUserService mockIUserService;

    @MockBean
    IEmailService iEmailService;

    private List<Order> orders;

    @Before
    public void setupTest() {
        orders = new ArrayList<>();
        when(mockOrderRepository.findAll())
                .thenReturn(orders);
    }

    @Test
    public void findAllOrders_whenOneOrder_returnOneOrder() {
        Order order = new Order();

        orders.add(order);

        List<OrderServiceModel> result = iOrderService.findAllOrders();

        assertEquals(1, result.size());
    }

    @Test
    public void findAllOrders_whenNoOrders_returnEmptyOrders() {
        orders.clear();
        List<OrderServiceModel> result = iOrderService.findAllOrders();
        assertTrue(result.isEmpty());
    }

    @Test(expected = OrderNotFoundException.class)
    public void findOrderById_whenNoOrders_throwException() throws OrderNotFoundException {
        orders.clear();
        iOrderService.findOrderById("testId");
    }

    @Test
    public void findOrderByCustomer_whenNoCustomer_returnEmptyOrders() {
        orders.clear();
        List<OrderServiceModel> ordersFound = iOrderService.findOrdersByCustomer("testCustomer");

        assertTrue(ordersFound.isEmpty());
    }

    @Test
    public void createOrder_isOrderSaved(){
        OrderServiceModel order = new OrderServiceModel();

        iOrderService.createOrder(order, new ArrayList<>(){{
            new RecordServiceModel();
        }});

        verify(mockOrderRepository)
                .saveAndFlush(any());
    }

    @Test
    public void findOneOrderInRange_returnOneOrderInRange(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        orders.add(new Order(){{ setRegisterDate(LocalDateTime.parse( "18/08/2000 23:59", dtf)); }});
        orders.add(new Order(){{ setRegisterDate(LocalDateTime.parse( "19/08/2000 12:00", dtf)); }});
        orders.add(new Order(){{ setRegisterDate(LocalDateTime.parse( "20/08/2000 00:00", dtf)); }});

        List<OrderServiceModel> ordersInRange = iOrderService.findOrdersInRange("19/08/2000", "19/08/2000");

        assertFalse(ordersInRange.isEmpty());
        assertEquals(ordersInRange.get(0).getRegisterDate(), LocalDateTime.parse( "19/08/2000 12:00", dtf));
    }

    @Test
    public void findNoOrdersInRange_returnNoOrdersInRange(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        orders.add(new Order(){{ setRegisterDate(LocalDateTime.parse( "17/08/2000 23:59", dtf)); }});
        orders.add(new Order(){{ setRegisterDate(LocalDateTime.parse( "18/08/2000 23:59", dtf)); }});
        orders.add(new Order(){{ setRegisterDate(LocalDateTime.parse( "20/08/2000 12:00", dtf)); }});
        orders.add(new Order(){{ setRegisterDate(LocalDateTime.parse( "21/08/2000 00:00", dtf)); }});

        List<OrderServiceModel> ordersInRange = iOrderService.findOrdersInRange("19/08/2000", "19/08/2000");

        assertTrue(ordersInRange.isEmpty());
    }
}
