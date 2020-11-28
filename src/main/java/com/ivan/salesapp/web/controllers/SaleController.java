package com.ivan.salesapp.web.controllers;

import com.ivan.salesapp.domain.models.view.SaleViewModel;
import com.ivan.salesapp.services.SaleService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sales")
public class SaleController extends BaseController{
    private final SaleService saleService;
    private final ModelMapper modelMapper;

    @Autowired
    public SaleController(SaleService saleService, ModelMapper modelMapper) {
        this.saleService = saleService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView allSales(Principal principal, ModelAndView modelAndView){
        modelAndView.addObject("categories",
                this.saleService.findAllSales().stream()
                        .map(s -> this.modelMapper
                                .map(s, SaleViewModel.class))
                        .collect(Collectors.toList()));
        return super.view("sale/all-sales", modelAndView);
    }

    @GetMapping("/register")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView registerSale(){
        //Doesn't need setting the view in ModelAndView, because of AJAX (using for fetch in add-product.html)
        return super.view("product/add-product");
    }
}
