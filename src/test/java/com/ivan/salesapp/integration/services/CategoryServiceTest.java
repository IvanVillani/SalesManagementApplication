package com.ivan.salesapp.integration.services;

import com.ivan.salesapp.domain.entities.Category;
import com.ivan.salesapp.domain.models.service.CategoryServiceModel;
import com.ivan.salesapp.exceptions.CategoryNotFoundException;
import com.ivan.salesapp.repository.CategoryRepository;
import com.ivan.salesapp.services.ICategoryService;
import com.ivan.salesapp.services.IProductService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CategoryServiceTest {
    @Autowired
    ICategoryService iCategoryService;

    @MockBean
    CategoryRepository mockCategoryRepository;

    @MockBean
    IProductService mockIProductSevice;

    private List<Category> categories;

    @Before
    public void setupTest() {
        categories = new ArrayList<>();
        when(mockCategoryRepository.findAll())
                .thenReturn(categories);
    }

    @Test
    public void addCategory_whenNoCategories_assertAdded() {
        iCategoryService.addCategory(new CategoryServiceModel());

        verify(mockCategoryRepository)
                .saveAndFlush(any());
    }

    @Test
    public void findAllCategories_whenOnePresent_returnOne() {
        categories.add(new Category(){{ setName("Test-Category"); }});

        List<CategoryServiceModel> result = iCategoryService.findAllCategories();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test-Category", result.get(0).getName());
    }

    @Test(expected = CategoryNotFoundException.class)
    public void findCategoryById_whenNoCategories_throwException() throws CategoryNotFoundException {
        iCategoryService.findCategoryById("test-id");
    }

    @Test(expected = CategoryNotFoundException.class)
    public void findCategoryByName_whenNoCategories_throwException() throws CategoryNotFoundException {
        iCategoryService.findCategoryByName("test-name");
    }

    @Test
    public void editCategory_whenCategoryPresent_returnEditedCategory() throws CategoryNotFoundException {
        when(mockCategoryRepository.findById(any()))
                .thenReturn(java.util.Optional.of(new Category()));

        CategoryServiceModel result = iCategoryService.editCategory("test-id", new CategoryServiceModel(){{ setName("test-name"); }});

        verify(mockCategoryRepository)
                .saveAndFlush(any());

        assertNotNull(result);
        assertEquals("test-name", result.getName());
    }

    @Test
    public void deleteCategory_whenCategoryPresent_assertDeleted() throws CategoryNotFoundException {
        when(mockCategoryRepository.findById(any()))
                .thenReturn(java.util.Optional.of(new Category()));

        iCategoryService.deleteCategory("test-id", mockIProductSevice);

        verify(mockCategoryRepository)
                .delete(any());
    }
}
