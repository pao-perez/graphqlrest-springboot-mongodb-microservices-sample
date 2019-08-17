package com.paoperez.categoryservice;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
class CategoryService {
    private final CategoryRepository categoryRepository;

    CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    Category getCategory(String id) throws CategoryNotFoundException {
        return categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
    }

    Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    Boolean updateCategory(Category category) throws CategoryNotFoundException {
        if (!categoryRepository.existsById(category.getId()))
            throw new CategoryNotFoundException(category.getId());
        categoryRepository.save(category);
        return true;
    }

    Boolean deleteCategory(String id) throws CategoryNotFoundException {
        if (!categoryRepository.existsById(id))
            throw new CategoryNotFoundException(id);
        categoryRepository.deleteById(id);
        return true;
    }

}
