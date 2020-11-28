package com.ivan.salesapp.services;

import com.ivan.salesapp.domain.entities.Sale;
import com.ivan.salesapp.domain.models.service.SaleServiceModel;
import com.ivan.salesapp.repository.SaleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaleServiceImpl implements SaleService{
    private final SaleRepository saleRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public SaleServiceImpl(SaleRepository saleRepository, ModelMapper modelMapper) {
        this.saleRepository = saleRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public SaleServiceModel registerSale(SaleServiceModel saleServiceModel) {
        Sale sale = this.modelMapper.map(saleServiceModel, Sale.class);

        return this.modelMapper.map(this.saleRepository.saveAndFlush(sale), SaleServiceModel.class);
    }

    @Override
    public List<SaleServiceModel> findAllSales() {
        return this.saleRepository.findAll().stream()
                .map(s -> this.modelMapper
                .map(s, SaleServiceModel.class)).collect(Collectors.toList());
    }
}
