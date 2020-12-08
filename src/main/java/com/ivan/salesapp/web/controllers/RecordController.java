package com.ivan.salesapp.web.controllers;

import com.ivan.salesapp.constants.ViewConstants;
import com.ivan.salesapp.domain.entities.Order;
import com.ivan.salesapp.domain.models.service.OrderServiceModel;
import com.ivan.salesapp.domain.models.service.RecordServiceModel;
import com.ivan.salesapp.domain.models.view.OfferViewModel;
import com.ivan.salesapp.domain.models.view.OrderViewModel;
import com.ivan.salesapp.domain.models.view.RecordViewModel;
import com.ivan.salesapp.domain.models.view.Sale;
import com.ivan.salesapp.exceptions.OrderNotFoundException;
import com.ivan.salesapp.exceptions.RecordNotFoundException;
import com.ivan.salesapp.services.IOrderService;
import com.ivan.salesapp.services.IRecordService;
import com.ivan.salesapp.services.IUserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;


@RestController
@RequestMapping("/records")
public class RecordController extends BaseController implements ViewConstants {
    private final IRecordService iRecordService;
    private final IOrderService iOrderService;
    private final IUserService iUserService;
    private final ModelMapper modelMapper;

    @Autowired
    public RecordController(IRecordService iRecordService, IOrderService iOrderService, IUserService iUserService, ModelMapper modelMapper) {
        this.iRecordService = iRecordService;
        this.iOrderService = iOrderService;
        this.iUserService = iUserService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/details/{id}")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView orderDetails(@PathVariable String id, ModelAndView modelAndView) throws RecordNotFoundException, OrderNotFoundException {
        List<RecordViewModel> models = this.iRecordService.retrieveRecordsByOrderId(id);

        OrderServiceModel orderServiceModel = this.iOrderService.findOrderById(id);

        modelAndView.addObject("records", models);
        modelAndView.addObject("order", orderServiceModel);

        return super.view(ORDER_RECORD, modelAndView);
    }

    @GetMapping("/sales")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView resellerSales(Principal principal, ModelAndView modelAndView) throws RecordNotFoundException {
        List<Sale> sales = this.iRecordService.retrieveSalesByUserUsername(principal.getName());

        modelAndView.addObject("sales", sales);

        return super.view(ORDER_ALL_SALES, modelAndView);
    }

    @PostMapping("/sales/search")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView searchSales(String start, String end, Principal principal, ModelAndView modelAndView) throws RecordNotFoundException {
        List<Sale> sales = this.iRecordService.retrieveSalesByUserUsername(principal.getName());

        modelAndView.addObject("sales", findSalesInRange(start, end, sales));

        return view(ORDER_ALL_SALES, modelAndView);
    }

    private List<Sale> findSalesInRange(String start, String end, List<Sale> sales){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        LocalDateTime startDateTime = LocalDateTime.parse(start + " 00:00", dtf);
        LocalDateTime endDateTime = LocalDateTime.parse(end + " 23:59", dtf);

        return sales
                .stream()
                .filter(sale -> startDateTime.isBefore(sale.getRegisterDate()) &&
                        endDateTime.isAfter(sale.getRegisterDate()))
                .sorted(Comparator.comparing(Sale::getRegisterDate, Comparator.reverseOrder()))
                .collect(toList());
    }

}