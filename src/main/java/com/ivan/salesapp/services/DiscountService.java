package com.ivan.salesapp.services;

import com.ivan.salesapp.constants.ExceptionMessageConstants;
import com.ivan.salesapp.domain.entities.Discount;
import com.ivan.salesapp.domain.entities.Product;
import com.ivan.salesapp.domain.entities.User;
import com.ivan.salesapp.domain.models.binding.DiscountAddBindingModel;
import com.ivan.salesapp.domain.models.service.DiscountServiceModel;
import com.ivan.salesapp.domain.models.service.ProductServiceModel;
import com.ivan.salesapp.domain.models.service.UserServiceModel;
import com.ivan.salesapp.repository.DiscountRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
public class DiscountService implements IDiscountService, ExceptionMessageConstants {

    private final DiscountRepository discountRepository;
    private final IProductService iProductService;
    private final ModelMapper modelMapper;

    @Autowired
    public DiscountService(DiscountRepository discountRepository, IProductService iProductService, ModelMapper modelMapper) {
        this.discountRepository = discountRepository;
        this.iProductService = iProductService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<DiscountServiceModel> findAllDiscounts() {
        return this.discountRepository.findAll()
                .stream()
                .map(o -> this.modelMapper.map(o, DiscountServiceModel.class))
                .collect(toList());
    }

    @Override
    public List<DiscountServiceModel> findAllDiscountsByCreatorUsername(String username) {
        return this.discountRepository.findAll()
                .stream()
                .filter(d -> username.equals(d.getCreator().getUsername()))
                .map(o -> this.modelMapper.map(o, DiscountServiceModel.class))
                .collect(toList());
    }

    @Override
    public List<DiscountServiceModel> findAllDiscountsByProductId(String id) {
        return this.discountRepository.findAll()
                .stream()
                .filter(d -> id.equals(d.getProduct().getId()))
                .map(o -> this.modelMapper.map(o, DiscountServiceModel.class))
                .collect(toList());
    }

    @Override
    public void discountProduct(String productId, DiscountAddBindingModel discountAddBindingModel, UserServiceModel userServiceModel) {
        Product product = iProductService.findAllProducts()
                .stream()
                .filter(p -> p.getId().equals(productId))
                .map(p -> modelMapper.map(p, Product.class))
                .collect(toList())
                .get(0);

        Discount discount = new Discount();

        discount.setCreator(this.modelMapper.map(userServiceModel, User.class));
        discount.setProduct(product);
        discount.setPrice(discountAddBindingModel.getPrice());

        this.discountRepository.saveAndFlush(discount);
    }

    @Override
    public void editProductDiscount(DiscountServiceModel model) {
        Discount discount = this.discountRepository.findById(model.getId())
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessageConstants.DISCOUNT_NOT_FOUND_EDIT));

        discount.setPrice(model.getPrice());

        this.discountRepository.saveAndFlush(discount);
    }

    @Override
    public void deleteDiscount(String id) {
        Discount discount = this.discountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessageConstants.DISCOUNT_NOT_FOUND_DELETE));

        this.discountRepository.delete(discount);
    }

    @Override
    public DiscountServiceModel findDiscountById(String id) {
        Discount discount = this.discountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessageConstants.DISCOUNT_NOT_FOUND));

        return this.modelMapper.map(discount, DiscountServiceModel.class);
    }

    @Override
    public void deleteDiscountsByUserId(String id) {
        List<Discount> discounts = this.discountRepository.findAll()
                .stream()
                .filter(d -> id.equals(d.getCreator().getId()))
                .collect(toList());

        for (Discount discount : discounts) {
            this.discountRepository.delete(discount);
        }
    }
}