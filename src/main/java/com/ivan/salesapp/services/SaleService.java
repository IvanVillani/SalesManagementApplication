package com.ivan.salesapp.services;

import com.ivan.salesapp.domain.entities.Order;
import com.ivan.salesapp.domain.models.service.SaleServiceModel;
import com.ivan.salesapp.repository.SaleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaleService implements ISaleService {
    private final SaleRepository saleRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public SaleService(SaleRepository saleRepository, ModelMapper modelMapper) {
        this.saleRepository = saleRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public SaleServiceModel registerSale(SaleServiceModel saleServiceModel) {
        Order order = this.modelMapper.map(saleServiceModel, Order.class);

        return this.modelMapper.map(this.saleRepository.saveAndFlush(order), SaleServiceModel.class);
    }

    @Override
    public List<SaleServiceModel> findAllSales() {
        return this.saleRepository.findAll().stream()
                .map(s -> this.modelMapper
                .map(s, SaleServiceModel.class)).collect(Collectors.toList());
    }
}
