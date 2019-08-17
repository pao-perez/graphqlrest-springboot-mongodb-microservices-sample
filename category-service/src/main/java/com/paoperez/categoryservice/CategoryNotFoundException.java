package com.paoperez.categoryservice;

class CategoryNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    CategoryNotFoundException(String id) {
        super(String.format("Category with id %s not found.", id));
    }
}
