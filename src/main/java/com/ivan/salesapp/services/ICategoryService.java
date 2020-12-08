package com.ivan.salesapp.services;

import com.ivan.salesapp.domain.models.service.CategoryServiceModel;
import com.ivan.salesapp.exceptions.CategoryNotFoundException;

import java.util.List;

public interface ICategoryService {
    void addCategory(CategoryServiceModel categoryServiceModel);

    List<CategoryServiceModel> findAllCategories();

    CategoryServiceModel findCategoryById(String id) throws CategoryNotFoundException;

    CategoryServiceModel findCategoryByName(String name) throws CategoryNotFoundException;

    void editCategory(String id, CategoryServiceModel categoryServiceModel) throws CategoryNotFoundException;

    void deleteCategory(String id, IProductService iProductService) throws CategoryNotFoundException;
}
