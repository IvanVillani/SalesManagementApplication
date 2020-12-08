package com.ivan.salesapp.constants;

public interface ExceptionMessageConstants {
    String USER_NOT_FOUND_BY_USERNAME = "There is no user with this username!";
    String USER_NOT_FOUND_BY_EMAIL = "There is no user with this email!";
    String USER_NOT_FOUND_BY_ID = "There is no user with wanted id!";

    String USERNAME_ALREADY_EXISTS = "There is already a user with this username!";
    String PASSWORDS_NOT_MATCHING = "New Password and Confirm New Password must be identical!";
    String INCORRECT_PASSWORD = "Incorrect password!";
    String EMAIL_ALREADY_EXISTS = "There is already a user with this email!";

    String CATEGORY_NOT_FOUND = "There is no category with this name or id!";
    String CATEGORY_IS_EMPTY = "There are no products in this category!";
    String CATEGORY_ALREADY_EXISTS = "There is already a category with this name!";

    String DISCOUNT_NOT_FOUND = "Discount with the wanted ID not found!";
    String DISCOUNT_NOT_FOUND_EDIT = "Edit unsuccessful! " + DISCOUNT_NOT_FOUND;
    String DISCOUNT_NOT_FOUND_DELETE = "Delete unsuccessful! " + DISCOUNT_NOT_FOUND;

    String NO_RESELLER_DISCOUNTS = "There are no discounts from this reseller!";

    String PRODUCT_NOT_FOUND_ID = "There is no product with wanted id!";
    String PRODUCT_TO_UPDATE_NOT_FOUND = "Product to update not found!";

    String ORDER_NOT_FOUND = "Order with id:%s not found!";

    String RECORD_BY_ORDER_ID_NOT_FOUND = "Record for the current order not found!";

    String OFFER_BY_USERNAME_NOT_FOUND = "Offer in record for %s not found!";

    String ITEM_WITH_SAME_PRODUCT = "Item in cart with same product not found!";
}
