package com.ivan.salesapp.services.validation;

import com.ivan.salesapp.domain.models.binding.CategoryAddBindingModel;
import com.ivan.salesapp.domain.models.binding.UserEditBindingModel;
import com.ivan.salesapp.domain.models.binding.UserRegisterBindingModel;
import com.ivan.salesapp.exceptions.InvalidCategoryException;
import com.ivan.salesapp.exceptions.InvalidUserException;

public interface ICategoryValidationService {
    boolean isNewCategoryValid(CategoryAddBindingModel category, String sourceView) throws InvalidCategoryException;

    boolean isEditedCategoryValid(CategoryAddBindingModel category, String id, String sourceView) throws InvalidCategoryException;
}
