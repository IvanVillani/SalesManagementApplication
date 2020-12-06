package com.ivan.salesapp.constants;

public interface ExceptionMessageConstants {
    String DISCOUNT_NOT_FOUND = "Discount with the wanted ID not found!";
    String DISCOUNT_NOT_FOUND_EDIT = "Edit unsuccessful! " + DISCOUNT_NOT_FOUND;
    String DISCOUNT_NOT_FOUND_DELETE = "Delete unsuccessful! " + DISCOUNT_NOT_FOUND;

    String PRODUCT_TO_UPDATE_NOT_FOUND = "Product to update not found!";

    String ORDER_NOT_FOUND = "Order with id:%s not found!";

    String RECORD_BY_ORDER_ID_NOT_FOUND = "Record for the current order not found!";

    String OFFER_BY_USERNAME_NOT_FOUND = "Offer in record for %s not found!";

    String ITEM_WITH_SAME_PRODUCT = "Item in cart with same product not found!";
}
