package com.ivan.salesapp.services;


import com.ivan.salesapp.domain.models.service.ProductServiceModel;
import com.ivan.salesapp.exceptions.ProductNotFoundException;

import java.util.List;

public interface IProductService {
    void addProduct(ProductServiceModel productServiceModel);

    List<ProductServiceModel> findAllProducts();

    ProductServiceModel findProductById(String id) throws ProductNotFoundException;

    void editProduct(String id, ProductServiceModel productServiceModel, List<String> categories) throws ProductNotFoundException;

    void deleteProduct(String id) throws ProductNotFoundException;

    List<ProductServiceModel> findAllByCategory(String category);
}
