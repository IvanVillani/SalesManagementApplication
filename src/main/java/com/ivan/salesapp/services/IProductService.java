package com.ivan.salesapp.services;


import com.ivan.salesapp.domain.models.service.ProductServiceModel;

import java.util.List;

public interface IProductService {
    ProductServiceModel addProduct(ProductServiceModel productServiceModel);

    List<ProductServiceModel> findAllProducts();

    ProductServiceModel findProductById(String id);

    ProductServiceModel editProduct(String id, ProductServiceModel productServiceModel, List<String> categories);

    void deleteProduct(String id);

    List<ProductServiceModel> findAllByCategory(String category);
}
