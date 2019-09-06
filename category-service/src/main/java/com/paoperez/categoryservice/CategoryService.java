package com.paoperez.categoryservice;

import java.util.Collection;

import org.springframework.stereotype.Service;

@Service
class CategoryService {
    private final CategoryRepository categoryRepository;

    CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    Collection<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    Category getCategory(String id) throws CategoryNotFoundException {
        return categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
    }

    Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    void updateCategory(Category category) throws CategoryNotFoundException {
        final String id = category.getId();

        if (!categoryRepository.existsById(id))
            throw new CategoryNotFoundException(id);
        categoryRepository.save(category);
    }

    void deleteCategory(String id) throws CategoryNotFoundException {
        if (!categoryRepository.existsById(id))
            throw new CategoryNotFoundException(id);
        categoryRepository.deleteById(id);
    }

}
