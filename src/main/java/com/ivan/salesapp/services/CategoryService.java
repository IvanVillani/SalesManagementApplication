package com.ivan.salesapp.services;

import com.ivan.salesapp.constants.ExceptionMessageConstants;
import com.ivan.salesapp.domain.entities.Category;
import com.ivan.salesapp.domain.models.service.CategoryServiceModel;
import com.ivan.salesapp.exceptions.CategoryNotFoundException;
import com.ivan.salesapp.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class CategoryService implements ICategoryService, ExceptionMessageConstants {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void addCategory(CategoryServiceModel categoryServiceModel) {
        Category category = this.modelMapper.map(categoryServiceModel, Category.class);

        this.categoryRepository.saveAndFlush(category);
    }

    @Override
    public List<CategoryServiceModel> findAllCategories() {
        return this.categoryRepository.findAll().stream()
                .map(c -> this.modelMapper.map(c, CategoryServiceModel.class)).collect(toList());
    }

    @Override
    public CategoryServiceModel findCategoryById(String id) throws CategoryNotFoundException {
        Category category = this.categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND));

        return this.modelMapper.map(category, CategoryServiceModel.class);
    }

    @Override
    public CategoryServiceModel findCategoryByName(String name) throws CategoryNotFoundException {
        Category category = this.categoryRepository.findByName(name)
                .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND));

        return this.modelMapper.map(category, CategoryServiceModel.class);
    }

    @Override
    public void editCategory(String id, CategoryServiceModel categoryServiceModel) throws CategoryNotFoundException {
        Category category = this.categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND));

        category.setName(categoryServiceModel.getName());


        this.categoryRepository.saveAndFlush(category);
    }

    @Override
    public void deleteCategory(String id, IProductService iProductService) throws CategoryNotFoundException {
        Category category = this.categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND));

        int allProductsByCategory = iProductService.findAllByCategory(category.getName()).size();

        if (allProductsByCategory != 0){
            throw new CategoryNotFoundException(CATEGORY_IS_EMPTY);
        }

        this.categoryRepository.delete(category);
    }
}
