package com.paoperez.categoryservice;

import java.util.Collection;

interface CategoryService {
    Collection<Category> getAllCategories();

    Category getCategory(final String id) throws CategoryNotFoundException;

    Category createCategory(final Category category) throws CategoryAlreadyExistsException;

    void updateCategory(final String id, final Category category)
            throws CategoryNotFoundException, CategoryAlreadyExistsException;

    void deleteCategory(final String id) throws CategoryNotFoundException;
}
