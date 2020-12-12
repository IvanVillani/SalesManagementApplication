package com.ivan.salesapp.services.validation;

import com.ivan.salesapp.domain.models.binding.UserEditBindingModel;
import com.ivan.salesapp.domain.models.binding.UserRegisterBindingModel;
import com.ivan.salesapp.exceptions.InvalidUserException;

public interface IUserValidationService {
    boolean isNewUserValid(UserRegisterBindingModel user, String sourceView) throws InvalidUserException;

    boolean isEditedUserValid(UserEditBindingModel user, String sourceView) throws InvalidUserException;
}
