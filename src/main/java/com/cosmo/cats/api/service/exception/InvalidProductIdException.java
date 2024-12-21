package com.cosmo.cats.api.service.exception;

public class InvalidProductIdException extends RuntimeException {
    public static final String INVALID_PRODUCT_ID_MESSAGE = "Invalid product ID format: %s";

    public InvalidProductIdException(String id) {
        super(String.format(INVALID_PRODUCT_ID_MESSAGE, id));
    }
}
