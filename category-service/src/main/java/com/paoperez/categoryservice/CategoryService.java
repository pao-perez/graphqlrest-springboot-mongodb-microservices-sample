package com.paoperez.categoryservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    Category getCategory(String id) {
        return categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
    }

    Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    Category updateCategory(Category category) {
        if (!categoryRepository.existsById(category.getId()))
            throw new CategoryNotFoundException(category.getId());
        return categoryRepository.save(category);
    }

    Boolean deleteCategory(String id) {
        if (!categoryRepository.existsById(id))
            throw new CategoryNotFoundException(id);
        categoryRepository.deleteById(id);
        return true;
    }

}
