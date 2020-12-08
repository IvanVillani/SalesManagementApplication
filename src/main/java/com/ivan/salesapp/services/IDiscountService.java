package com.ivan.salesapp.services;

import com.ivan.salesapp.domain.models.binding.DiscountAddBindingModel;
import com.ivan.salesapp.domain.models.service.DiscountServiceModel;
import com.ivan.salesapp.domain.models.service.ProductServiceModel;
import com.ivan.salesapp.domain.models.service.UserServiceModel;
import com.ivan.salesapp.exceptions.DiscountNotFoundException;

import java.util.List;

public interface IDiscountService {

    List<DiscountServiceModel> findAllDiscounts();

    List<DiscountServiceModel> findAllDiscountsByCreatorUsername(String username);

    List<DiscountServiceModel> findAllDiscountsByProductId(String id);

    DiscountServiceModel findDiscountById(String id) throws DiscountNotFoundException;

    DiscountServiceModel discountProduct(String productId, DiscountAddBindingModel discountAddBindingModel, UserServiceModel userServiceModel);

    void editProductDiscount(DiscountServiceModel discountServiceModel) throws DiscountNotFoundException;

    void deleteDiscount(String id) throws DiscountNotFoundException;

    void deleteDiscountsByUserId(String id);
}