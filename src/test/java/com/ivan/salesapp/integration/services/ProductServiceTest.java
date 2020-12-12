package com.ivan.salesapp.integration.services;

import com.ivan.salesapp.domain.entities.Product;
import com.ivan.salesapp.domain.models.service.CategoryServiceModel;
import com.ivan.salesapp.domain.models.service.ProductServiceModel;
import com.ivan.salesapp.exceptions.ProductNotFoundException;
import com.ivan.salesapp.repository.ProductRepository;
import com.ivan.salesapp.services.ICategoryService;
import com.ivan.salesapp.services.IProductService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ProductServiceTest {
    @Autowired
    IProductService iProductService;

    @MockBean
    ProductRepository mockProductRepository;

    @MockBean
    ICategoryService mockICategoryService;

    private List<Product> products;

    private List<CategoryServiceModel> categories;

    @Before
    public void setupTest() {
        products = new ArrayList<>();
        when(mockProductRepository.findAll())
                .thenReturn(products);

        categories = new ArrayList<>();
        when(mockICategoryService.findAllCategories())
                .thenReturn(categories);
    }

    @Test
    public void createProduct_isProductSaved(){
        ProductServiceModel product = new ProductServiceModel();

        iProductService.addProduct(product);
        verify(mockProductRepository)
                .saveAndFlush(any());
    }

    @Test
    public void findAllProducts_whenNoProducts_returnEmptyProducts() {
        products.clear();
        List<ProductServiceModel> result = iProductService.findAllProducts();
        assertTrue(result.isEmpty());
    }

    @Test(expected = ProductNotFoundException.class)
    public void findProductById_whenNoProducts_throwException() throws ProductNotFoundException {
        products.clear();
        iProductService.findProductById("testId");
    }

    @Test
    public void editProduct_whenProductPresent_returnEditedProduct() throws ProductNotFoundException {
        categories.add(new CategoryServiceModel(){{
            setId("testId");
            setName("Test-category");
        }});

        when(mockProductRepository.findById(any()))
                .thenReturn(java.util.Optional.of(new Product()));

        ProductServiceModel newProduct = new ProductServiceModel(){{
           setName("Test-name");
           setDescription("Test-description");
           setPrice(BigDecimal.valueOf(222));
           setStock(222);
        }};

        ProductServiceModel result = iProductService.editProduct("id", newProduct, List.of("testId"));

        verify(mockProductRepository)
                .saveAndFlush(any());

        assertNotNull(result);
        assertEquals("Test-category", result.getCategories().get(0).getName());
    }

    @Test(expected = ProductNotFoundException.class)
    public void deleteProduct_whenNoProducts_throwException() throws ProductNotFoundException {
        products.clear();
        iProductService.deleteProduct("testId");
    }

    @Test
    public void deleteProduct_whenProductPresent_executeDelete() throws ProductNotFoundException {
        products.clear();

        when(mockProductRepository.findById(any()))
                .thenReturn(java.util.Optional.of(new Product()));

        iProductService.deleteProduct("testId");

        verify(mockProductRepository)
                .delete(any());
    }
}
