package com.paoperez.categoryservice;

import java.util.Collection;

interface CategoryService {
  Collection<Category> getAllCategories();

  Category getCategory(String id) throws CategoryNotFoundException;

  String createCategory(Category category) throws CategoryAlreadyExistsException;

  void updateCategory(String id, Category category)
      throws CategoryNotFoundException, CategoryAlreadyExistsException, CategoryMismatchException;

  void deleteCategory(String id) throws CategoryNotFoundException;
}
