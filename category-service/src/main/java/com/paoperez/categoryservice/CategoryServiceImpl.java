package com.paoperez.categoryservice;

import java.util.Collection;

import org.springframework.stereotype.Service;

@Service
final class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    CategoryServiceImpl(final CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Collection<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategory(final String id) throws CategoryNotFoundException {
        return categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
    }

    public Category createCategory(final Category category) throws CategoryAlreadyExistsException {
        final String categoryName = category.getName();

        if (categoryRepository.findByName(categoryName) != null)
            throw new CategoryAlreadyExistsException(categoryName);

        return categoryRepository.save(category);
    }

    public void updateCategory(final String id, final Category category)
            throws CategoryNotFoundException, CategoryAlreadyExistsException {
        categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));

        final String categoryName = category.getName();
        final Category foundCategory = categoryRepository.findByName(categoryName);
        if (foundCategory != null && !foundCategory.getId().equals(id))
            throw new CategoryAlreadyExistsException(categoryName);

        Category updatedCategory = Category.builder().name(categoryName).id(id).build();
        categoryRepository.save(updatedCategory);
    }

    public void deleteCategory(final String id) throws CategoryNotFoundException {
        categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
        categoryRepository.deleteById(id);
    }

}
