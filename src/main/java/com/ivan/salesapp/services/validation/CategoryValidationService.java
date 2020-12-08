package com.ivan.salesapp.services.validation;

import com.ivan.salesapp.constants.ExceptionMessageConstants;
import com.ivan.salesapp.domain.models.binding.CategoryAddBindingModel;
import com.ivan.salesapp.exceptions.CategoryNotFoundException;
import com.ivan.salesapp.exceptions.InvalidCategoryException;
import com.ivan.salesapp.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryValidationService implements ICategoryValidationService, ExceptionMessageConstants {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryValidationService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public boolean isNewCategoryValid(CategoryAddBindingModel category, String sourceView) throws InvalidCategoryException {
        try {
            this.categoryRepository.findByName(category.getName())
                    .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND));

            throw new InvalidCategoryException(CATEGORY_ALREADY_EXISTS, sourceView, "", category.getName());
        } catch (CategoryNotFoundException e) {
            return true;
        }
    }

    @Override
    public boolean isEditedCategoryValid(CategoryAddBindingModel category, String id, String sourceView) throws InvalidCategoryException {
        try {
            this.categoryRepository.findByName(category.getName())
                    .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND));

            throw new InvalidCategoryException(CATEGORY_ALREADY_EXISTS, sourceView, id, "");
        } catch (CategoryNotFoundException e) {
            return true;
        }
    }
}
