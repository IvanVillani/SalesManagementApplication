package com.ivan.salesapp.services;

import com.ivan.salesapp.domain.entities.Category;
import com.ivan.salesapp.domain.models.service.CategoryServiceModel;
import com.ivan.salesapp.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class CategoryService implements ICategoryService {
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
    public CategoryServiceModel findCategoryById(String id) {
        Category category = this.categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incorrect id!"));

        return this.modelMapper.map(category, CategoryServiceModel.class);
    }

    @Override
    public void editCategory(String id, CategoryServiceModel categoryServiceModel) {
        Category category = this.categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incorrect id!"));

        category.setName(categoryServiceModel.getName());


        this.categoryRepository.saveAndFlush(category);
    }

    @Override
    public void deleteCategory(String id, IProductService iProductService) {
        Category category = this.categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incorrect id!"));

        int allProductsByCategory = iProductService.findAllByCategory(category.getName()).size();

        if (allProductsByCategory != 0){
            throw new IllegalArgumentException("There are no products in this category!");
        }

        this.categoryRepository.delete(category);
    }
}
