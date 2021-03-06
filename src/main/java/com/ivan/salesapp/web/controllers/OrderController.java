package com.ivan.salesapp.web.controllers;

import com.ivan.salesapp.constants.RoleConstants;
import com.ivan.salesapp.constants.ViewConstants;
import com.ivan.salesapp.domain.models.service.OrderServiceModel;
import com.ivan.salesapp.domain.models.service.RecordServiceModel;
import com.ivan.salesapp.domain.models.view.OrderViewModel;
import com.ivan.salesapp.domain.models.view.RecordViewModel;
import com.ivan.salesapp.exceptions.OrderNotFoundException;
import com.ivan.salesapp.exceptions.RecordNotFoundException;
import com.ivan.salesapp.services.IOrderService;
import com.ivan.salesapp.services.IRecordService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/orders")
public class OrderController extends BaseController implements RoleConstants, ViewConstants {
    private final IOrderService iOrderService;
    private final IRecordService iRecordService;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderController(IOrderService iOrderService, IRecordService iRecordService, ModelMapper modelMapper) {
        this.iOrderService = iOrderService;
        this.iRecordService = iRecordService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/all")
    @PreAuthorize(ROLE_ADMIN)
    public ModelAndView getAllOrders(ModelAndView modelAndView) {
        List<OrderViewModel> orderViewModels = iOrderService.findAllOrders()
                .stream()
                .map(o -> modelMapper.map(o, OrderViewModel.class))
                .collect(toList());

        modelAndView.addObject("orders", orderViewModels);

        return view(ORDER_ALL, modelAndView);
    }

    @GetMapping("/all/details/{id}")
    @PreAuthorize(ROLE_ADMIN)
    public ModelAndView allOrderDetails(@PathVariable String id, ModelAndView modelAndView) throws RecordNotFoundException {
        RecordViewModel record = this.modelMapper.map(this.iRecordService.retrieveRecordsByOrderId(id), RecordViewModel.class);

        modelAndView.addObject("record", record);

        return super.view(ORDER_DETAILS, modelAndView);
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView getMyOrders(ModelAndView modelAndView, Principal principal) {
        List<OrderServiceModel> orderServiceModels = iOrderService.findOrdersByCustomer(principal.getName());
        List<OrderViewModel> orderViewModels = orderServiceModels.stream()
                .map(o -> modelMapper.map(o, OrderViewModel.class))
                .collect(toList());

        modelAndView.addObject("orders", orderViewModels);

        return view(ORDER_ALL, modelAndView);
    }

    @GetMapping("/my/details/{id}")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView myOrderDetails(@PathVariable String id, ModelAndView modelAndView) throws OrderNotFoundException {
        OrderViewModel orderViewModel = this.modelMapper.map(this.iOrderService.findOrderById(id), OrderViewModel.class);
        modelAndView.addObject("order", orderViewModel);

        return super.view(ORDER_DETAILS, modelAndView);
    }

    @PostMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView searchOrders(String start, String end, ModelAndView modelAndView) {
        List<OrderViewModel> orderViewModels = iOrderService.findOrdersInRange(start, end)
                .stream()
                .map(o -> modelMapper.map(o, OrderViewModel.class))
                .collect(toList());

        modelAndView.addObject("orders", orderViewModels);

        return view(ORDER_ALL, modelAndView);
    }
}