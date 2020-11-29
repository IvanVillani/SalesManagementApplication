package com.ivan.salesapp.services;

import com.ivan.salesapp.domain.models.service.ProductServiceModel;
import com.ivan.salesapp.domain.models.service.SaleServiceModel;

import java.util.List;

public interface ISaleService {
    SaleServiceModel registerSale(SaleServiceModel saleServiceModel);

    List<SaleServiceModel> findAllSales();

//    ProductServiceModel findProductById(String id);
//
//    ProductServiceModel editProduct(String id, ProductServiceModel productServiceModel);
//
//    void deleteProduct(String id);
}
