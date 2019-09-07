package com.paoperez.categoryservice;

class CategoryAlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    CategoryAlreadyExistsException(final String name) {
        super(String.format("Category with name %s already exists.", name));
    }
}
