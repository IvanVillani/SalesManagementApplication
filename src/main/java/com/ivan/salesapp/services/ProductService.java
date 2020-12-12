package com.ivan.salesapp.services;

import com.ivan.salesapp.constants.ExceptionMessageConstants;
import com.ivan.salesapp.domain.entities.Category;
import com.ivan.salesapp.domain.entities.Product;
import com.ivan.salesapp.domain.models.service.CategoryServiceModel;
import com.ivan.salesapp.domain.models.service.ProductServiceModel;
import com.ivan.salesapp.exceptions.ProductNotFoundException;
import com.ivan.salesapp.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
public class ProductService implements IProductService, ExceptionMessageConstants {
    private final ProductRepository productRepository;
    private final ICategoryService iCategoryService;
    private final ModelMapper modelMapper;

    @Autowired
    public ProductService(ProductRepository productRepository, ICategoryService iCategoryService, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.iCategoryService = iCategoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public void addProduct(ProductServiceModel productServiceModel) {
        Product product = this.modelMapper.map(productServiceModel, Product.class);

        this.productRepository.saveAndFlush(product);
    }

    @Override
    public List<ProductServiceModel> findAllProducts() {
        return this.productRepository.findAll().stream()
                .map(p -> this.modelMapper
                        .map(p, ProductServiceModel.class)).collect(toList());
    }

    @Override
    public ProductServiceModel findProductById(String id) throws ProductNotFoundException {
        Product product = this.productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_ID));

        return this.modelMapper.map(product, ProductServiceModel.class);
    }

    @Override
    public ProductServiceModel editProduct(String id, ProductServiceModel productServiceModel, List<String> categories) throws ProductNotFoundException {
        Product product = this.productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_ID));

        product.setName(productServiceModel.getName());
        product.setDescription(productServiceModel.getDescription());
        product.setPrice(productServiceModel.getPrice());
        product.setStock(productServiceModel.getStock());

        List<Category> newCategories = this.iCategoryService.findAllCategories()
                .stream()
                .filter(c -> categories.contains(c.getId()))
                .map(c -> this.modelMapper.map(c, Category.class))
                .collect(toList());

        product.setCategories(newCategories);


        this.productRepository.saveAndFlush(product);

        return this.modelMapper.map(product, ProductServiceModel.class);
    }

    @Override
    public void deleteProduct(String id) throws ProductNotFoundException {
        Product product = this.productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_ID));

        this.productRepository.delete(product);
    }

    @Override
    public List<ProductServiceModel> findAllByCategory(String category) {
        return this.productRepository.findAll()
                .stream()
                .filter(filterProductStreamByMatchingCategoryName(category))
                .map(product -> this.modelMapper.map(product, ProductServiceModel.class))
                .collect(toList());
    }

    private static Predicate<Product> filterProductStreamByMatchingCategoryName(String category){
        return product -> product.getCategories()
                .stream()
                .anyMatch(categoryStream -> categoryStream.getName().equals(category));
    }
}
