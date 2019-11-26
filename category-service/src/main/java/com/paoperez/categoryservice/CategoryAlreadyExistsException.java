package com.paoperez.categoryservice;

class CategoryAlreadyExistsException extends Exception {
  private static final long serialVersionUID = 1L;

  CategoryAlreadyExistsException(final String name) {
    super(String.format("Category with name %s already exists.", name));
  }
}
