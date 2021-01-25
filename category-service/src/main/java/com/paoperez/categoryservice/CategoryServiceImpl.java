package com.paoperez.categoryservice;

import java.util.Collection;
import java.util.Optional;
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

  public String createCategory(final Category category) throws CategoryAlreadyExistsException {
    final String categoryName = category.getName();

    if (categoryRepository.findByName(categoryName) != null) {
      throw new CategoryAlreadyExistsException(categoryName);
    }

    return categoryRepository.save(category).getId();
  }

  public void updateCategory(final String id, final Category category)
      throws CategoryNotFoundException, CategoryAlreadyExistsException, CategoryMismatchException {
    Optional<Category> retrievedCategory = categoryRepository.findById(id);
    if (retrievedCategory.isEmpty()) {
      throw new CategoryNotFoundException(id);
    }

    String categoryId = category.getId();
    if (!id.equals(categoryId)) {
      throw new CategoryMismatchException(id, categoryId);
    }

    final String categoryName = category.getName();
    final Category categoryFromName = categoryRepository.findByName(categoryName);
    if (categoryFromName != null && !categoryFromName.getId().equals(id)) {
      throw new CategoryAlreadyExistsException(categoryName);
    }

    categoryRepository.save(category);
  }

  public void deleteCategory(final String id) throws CategoryNotFoundException {
    if (categoryRepository.findById(id).isEmpty()) {
      throw new CategoryNotFoundException(id);
    }
    categoryRepository.deleteById(id);
  }
}
