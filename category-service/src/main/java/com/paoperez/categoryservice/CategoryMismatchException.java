package com.paoperez.categoryservice;

class CategoryMismatchException extends Exception {
    private static final long serialVersionUID = 1L;

    CategoryMismatchException(String id, String categoryId) {
        super(String.format("Category with id %s does not match category argument %s.", id,
                categoryId));
    }
}
