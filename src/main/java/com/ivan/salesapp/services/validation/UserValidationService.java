package com.ivan.salesapp.services.validation;

import com.ivan.salesapp.constants.ExceptionMessageConstants;
import com.ivan.salesapp.domain.entities.User;
import com.ivan.salesapp.domain.models.binding.UserEditBindingModel;
import com.ivan.salesapp.domain.models.binding.UserRegisterBindingModel;
import com.ivan.salesapp.exceptions.InvalidUserException;
import com.ivan.salesapp.exceptions.UserNotFoundException;
import com.ivan.salesapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserValidationService implements IUserValidationService, ExceptionMessageConstants {
    private final UserRepository userRepository;

    @Autowired
    public UserValidationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isNewUserValid(UserRegisterBindingModel user, String sourceView) throws InvalidUserException {
        try {
            if (!user.getPassword().equals(user.getConfirmPassword())){
                throw new InvalidUserException(PASSWORDS_NOT_MATCHING, "", sourceView);
            }

            this.userRepository.findByUsername(user.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_BY_USERNAME));

            throw new InvalidUserException(USERNAME_ALREADY_EXISTS, "", sourceView);
        } catch (UsernameNotFoundException e) {
            try {
                this.userRepository.findByEmail(user.getEmail())
                        .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_BY_EMAIL));

                throw new InvalidUserException(EMAIL_ALREADY_EXISTS, "", sourceView);
            } catch (UserNotFoundException emailNotFoundException) {
                return true;
            }
        }
    }

    @Override
    public boolean isEditedUserValid(UserEditBindingModel user, String sourceView) throws InvalidUserException {
        try {
            if (!user.getPassword().equals(user.getConfirmPassword())){
                throw new InvalidUserException(PASSWORDS_NOT_MATCHING, user.getUsername(), sourceView);
            }

            User repoUser = this.userRepository.findByEmail(user.getEmail())
                    .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_BY_EMAIL));

            if(user.getUsername().equals(repoUser.getUsername())){
                throw new UserNotFoundException(USER_NOT_FOUND_BY_EMAIL);
            }

            throw new InvalidUserException(EMAIL_ALREADY_EXISTS, user.getUsername(), sourceView);
        } catch (UserNotFoundException emailNotFoundException) {
            return true;
        }
    }
}
