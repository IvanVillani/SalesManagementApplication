package com.ivan.salesapp.integration.services;

import com.ivan.salesapp.domain.entities.*;
import com.ivan.salesapp.domain.models.binding.DiscountAddBindingModel;
import com.ivan.salesapp.domain.models.service.CategoryServiceModel;
import com.ivan.salesapp.domain.models.service.DiscountServiceModel;
import com.ivan.salesapp.domain.models.service.ProductServiceModel;
import com.ivan.salesapp.domain.models.service.UserServiceModel;
import com.ivan.salesapp.exceptions.CategoryNotFoundException;
import com.ivan.salesapp.exceptions.DiscountNotFoundException;
import com.ivan.salesapp.repository.DiscountRepository;
import com.ivan.salesapp.repository.ProductRepository;
import com.ivan.salesapp.services.IDiscountService;
import com.ivan.salesapp.services.IProductService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DiscountServiceTest {
    @Autowired
    IDiscountService iDiscountService;

    @MockBean
    DiscountRepository mockDiscountRepository;

    @MockBean
    IProductService mockIProductService;

    private List<Discount> discounts;

    private List<ProductServiceModel> products;

    @Before
    public void setupTest() {
        discounts = new ArrayList<>();
        products = new ArrayList<>();
        when(mockDiscountRepository.findAll())
                .thenReturn(discounts);

        when(mockIProductService.findAllProducts())
                .thenReturn(products);
    }

    @Test
    public void findAllDiscounts_whenOnePresent_returnOne() {
        Product testProduct = new Product(){{
            setCategories(new ArrayList<>(){{
                add(new Category());
            }});
        }};

        User testUser = new User(){{
            setAuthorities(new LinkedHashSet<>(){{
                add(new Role());
            }});
        }};

        discounts.add(new Discount(){{ setProduct(testProduct); setCreator(testUser);}});

        List<DiscountServiceModel> result = iDiscountService.findAllDiscounts();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    public void discountProduct_whenProductPresent_returnDiscount() {
        ProductServiceModel testProduct = new ProductServiceModel(){{
            setId("test-id");
            setCategories(new ArrayList<>(){{
                add(new CategoryServiceModel());
            }});
        }};

        products.add(testProduct);

        DiscountServiceModel result = iDiscountService
                .discountProduct("test-id", new DiscountAddBindingModel(){{ setPrice(new BigDecimal(100)); }},
                        new UserServiceModel());

        assertNotNull(result);
        assertEquals(new BigDecimal(100), result.getPrice());
    }

    @Test
    public void editDiscount_whenDiscountPresent_returnEditedDiscount()throws DiscountNotFoundException {
        Product testProduct = new Product(){{
            setCategories(new ArrayList<>(){{
                add(new Category());
            }});
        }};

        User testUser = new User(){{
            setAuthorities(new LinkedHashSet<>(){{
                add(new Role());
            }});
        }};

        Discount testDiscount = new Discount(){{ setProduct(testProduct); setCreator(testUser);}};

        when(mockDiscountRepository.findById(any()))
                .thenReturn(java.util.Optional.of(testDiscount));

        DiscountServiceModel result = iDiscountService
                .editProductDiscount(new DiscountServiceModel(){{ setPrice(new BigDecimal(200)); }});

        verify(mockDiscountRepository)
                .saveAndFlush(any());

        assertNotNull(result);
        assertEquals(new BigDecimal(200), result.getPrice());
    }

    @Test(expected = DiscountNotFoundException.class)
    public void editDiscount_whenNoDiscountPresent_throwException()throws DiscountNotFoundException {
        iDiscountService.editProductDiscount(new DiscountServiceModel(){{ setPrice(new BigDecimal(200)); }});
    }

    @Test
    public void deleteDiscount_whenDiscountPresent_assertDeleted() throws DiscountNotFoundException {
        when(mockDiscountRepository.findById(any()))
                .thenReturn(java.util.Optional.of(new Discount()));

        iDiscountService.deleteDiscount("test-id");

        verify(mockDiscountRepository)
                .delete(any());
    }

    @Test(expected = DiscountNotFoundException.class)
    public void deleteDiscount_whenNoDiscountPresent_throwException() throws DiscountNotFoundException {
        iDiscountService.deleteDiscount("test-id");
    }
}
