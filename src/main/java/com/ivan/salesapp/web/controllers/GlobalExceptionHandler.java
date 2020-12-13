package com.ivan.salesapp.web.controllers;

import com.ivan.salesapp.constants.ViewConstants;
import com.ivan.salesapp.domain.models.view.CategoryViewModel;
import com.ivan.salesapp.domain.models.view.DiscountViewModel;
import com.ivan.salesapp.domain.models.view.UserProfileViewModel;
import com.ivan.salesapp.exceptions.CategoryNotFoundException;
import com.ivan.salesapp.exceptions.InvalidCategoryException;
import com.ivan.salesapp.exceptions.InvalidUserException;
import com.ivan.salesapp.exceptions.NoResellerDiscountsException;
import com.ivan.salesapp.services.ICategoryService;
import com.ivan.salesapp.services.IDiscountService;
import com.ivan.salesapp.services.IUserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;

import static java.util.stream.Collectors.toList;

@ControllerAdvice
public class GlobalExceptionHandler extends BaseController implements ViewConstants {
    private final IUserService iUserService;
    private final ICategoryService iCategoryService;
    private final IDiscountService iDiscountService;
    private final ModelMapper modelMapper;

    @Autowired
    public GlobalExceptionHandler(IUserService iUserService, ICategoryService iCategoryService, IDiscountService iDiscountService, ModelMapper modelMapper) {
        this.iUserService = iUserService;
        this.iCategoryService = iCategoryService;
        this.iDiscountService = iDiscountService;
        this.modelMapper = modelMapper;
    }

    @ExceptionHandler(InvalidUserException.class)
    public ModelAndView handleInvalidUser(InvalidUserException ex){
        ModelAndView modelAndView = new ModelAndView();
        if(!ex.getUsername().isEmpty()){
            modelAndView.addObject("model", this.modelMapper
                    .map(this.iUserService.findUserByUsername(ex.getUsername()), UserProfileViewModel.class));
        }

        modelAndView.addObject("error", ex.getMessage());

        return super.view(ex.getSourceView(), modelAndView);
    }

    @ExceptionHandler(InvalidCategoryException.class)
    public ModelAndView handleInvalidCategory(InvalidCategoryException ex) throws CategoryNotFoundException {
        ModelAndView modelAndView = new ModelAndView();
        if (ex.getId().isEmpty()){
            modelAndView.addObject("model", this.modelMapper
                    .map(this.iCategoryService.findCategoryByName(ex.getName()), CategoryViewModel.class));
        }else {
            modelAndView.addObject("model", this.modelMapper
                    .map(this.iCategoryService.findCategoryById(ex.getId()), CategoryViewModel.class));
        }

        modelAndView.addObject("error", ex.getMessage());

        return super.view(ex.getSourceView(), modelAndView);
    }

    @ExceptionHandler(NoResellerDiscountsException.class)
    public ModelAndView handleNoResellerDiscounts(NoResellerDiscountsException ex) {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("discounts", this.iDiscountService.findAllDiscounts()
                .stream()
                .map(d -> this.modelMapper.map(d, DiscountViewModel.class))
                .collect(toList()));

        modelAndView.addObject("error", ex.getMessage());

        return super.view(ex.getSourceView(), modelAndView);
    }

    @ExceptionHandler(Throwable.class)
    public ModelAndView handleOthers(Throwable ex){
        //System.out.println(Arrays.toString(ex.getStackTrace()));
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("error", ex.getMessage());

        return super.view(ERROR, modelAndView);
    }
}
