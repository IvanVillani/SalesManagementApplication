package com.ivan.salesapp.services;

import com.ivan.salesapp.domain.models.binding.DiscountAddBindingModel;
import com.ivan.salesapp.domain.models.service.DiscountServiceModel;
import com.ivan.salesapp.domain.models.service.ProductServiceModel;
import com.ivan.salesapp.domain.models.service.UserServiceModel;

import java.util.List;

public interface IDiscountService {

    List<DiscountServiceModel> findAllDiscounts();

    List<DiscountServiceModel> findAllDiscountsByCreatorUsername(String username);

    List<DiscountServiceModel> findAllDiscountsByProductId(String id);

    DiscountServiceModel findDiscountById(String id);

    void discountProduct(String productId, DiscountAddBindingModel discountAddBindingModel, UserServiceModel userServiceModel);

    void editProductDiscount(DiscountServiceModel discountServiceModel);

    void deleteDiscount(String id);

    void deleteDiscountsByUserId(String id);
}