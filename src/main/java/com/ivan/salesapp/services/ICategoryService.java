package com.ivan.salesapp.services;

import com.ivan.salesapp.domain.models.service.CategoryServiceModel;

import java.util.List;

public interface ICategoryService {
    CategoryServiceModel addCategory(CategoryServiceModel categoryServiceModel);

    List<CategoryServiceModel> findAllCategories();

    CategoryServiceModel findCategoryById(String id);

    CategoryServiceModel editCategory(String id, CategoryServiceModel categoryServiceModel);

    void deleteCategory(String id, IProductService iProductService);
}
