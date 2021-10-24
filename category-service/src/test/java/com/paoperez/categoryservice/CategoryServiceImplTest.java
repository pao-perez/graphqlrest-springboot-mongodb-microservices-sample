package com.paoperez.categoryservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class CategoryServiceImplTest {
  private CategoryService service;

  @MockBean
  private CategoryRepository repository;

  @BeforeEach
  void init() {
    service = new CategoryServiceImpl(repository);
  }

  @Test
  void getAllCategories_shouldReturnCategories() {
    Category categoryA = new Category();
    categoryA.setId("A");
    categoryA.setName("Blog");
    Category categoryB = new Category();
    categoryB.setId("B");
    categoryB.setName("Tutorial");
    List<Category> expected = ImmutableList.of(categoryA, categoryB);
    when(repository.findAll()).thenReturn(expected);

    Collection<Category> actual = service.getAllCategories();

    assertEquals(expected, actual);
    verify(repository, times(1)).findAll();
  }

  @Test
  void getCategory_whenExistingId_shouldReturnCategory() throws CategoryNotFoundException {
    String existingId = "A";
    Category expected = new Category();
    expected.setId(existingId);
    expected.setName("Blog");
    when(repository.findById(existingId)).thenReturn(Optional.of(expected));

    Category actual = service.getCategory(existingId);

    assertEquals(expected, actual);
    verify(repository, times(1)).findById(existingId);
  }

  @Test
  void getCategory_whenNonexistingId_shouldThrowNotFoundException() {
    String nonExistingId = "Z";
    Optional<Category> nonExistingCategory = Optional.empty();
    when(repository.findById(nonExistingId)).thenReturn(nonExistingCategory);

    Exception actual =
        assertThrows(CategoryNotFoundException.class, () -> service.getCategory(nonExistingId));

    String expected = String.format("Category with id %s not found.", nonExistingId);
    assertEquals(expected, actual.getMessage());
    verify(repository, times(1)).findById(nonExistingId);
  }

  @Test
  void createCategory_whenNonexistingName_shouldReturnCreatedCategory()
      throws CategoryAlreadyExistsException {
    String nonExistingName = "Blog";
    when(repository.findByName(nonExistingName)).thenReturn(null);
    Category category = new Category();
    category.setName(nonExistingName);
    Category expected = new Category();
    expected.setName(nonExistingName);
    expected.setId("A");
    when(repository.save(category)).thenReturn(expected);

    String actual = service.createCategory(category);

    assertEquals(expected.getId(), actual);
    verify(repository, times(1)).findByName(nonExistingName);
    verify(repository, times(1)).save(category);
  }

  @Test
  void createCategory_whenExistingName_shouldThrowAlreadyExistsException() {
    String existingName = "Blog";
    Category existingCategory = new Category();
    existingCategory.setName(existingName);
    existingCategory.setId("A");
    when(repository.findByName(existingName)).thenReturn(existingCategory);
    Category category = new Category();
    category.setName(existingName);
    category.setId("B");

    Exception actual =
        assertThrows(CategoryAlreadyExistsException.class, () -> service.createCategory(category));

    String expected = String.format("Category with name %s already exists.", existingName);
    assertEquals(expected, actual.getMessage());
    verify(repository, times(1)).findByName(existingName);
    verify(repository, times(0)).save(null);
  }

  @Test
  void updateCategory_whenExistingIdAndNonexistingName_shouldNotThrowException()
      throws CategoryNotFoundException, CategoryAlreadyExistsException, CategoryMismatchException {
    String existingId = "A";
    Category retrievedCategory = new Category();
    retrievedCategory.setId(existingId);
    retrievedCategory.setName("Old");
    when(repository.findById(existingId)).thenReturn(Optional.of(retrievedCategory));
    String nonExistingName = "New";
    when(repository.findByName(nonExistingName)).thenReturn(null);
    Category updateCategory = new Category();
    updateCategory.setId(existingId);
    updateCategory.setName(nonExistingName);
    Category expected = new Category();
    expected.setId(existingId);
    expected.setName(nonExistingName);
    when(repository.save(updateCategory)).thenReturn(expected);

    service.updateCategory(existingId, updateCategory);

    verify(repository, times(1)).findById(existingId);
    verify(repository, times(1)).findByName(nonExistingName);
    verify(repository, times(1)).save(updateCategory);
  }

  @Test
  void updateCategory_whenNonexistingId_shouldThrowNotFoundException() {
    String nonExistingId = "A";
    Optional<Category> nonExistingCategory = Optional.empty();
    when(repository.findById(nonExistingId)).thenReturn(nonExistingCategory);
    Category updateCategory = new Category();
    updateCategory.setId(nonExistingId);
    updateCategory.setName("New");

    Exception actual = assertThrows(CategoryNotFoundException.class,
        () -> service.updateCategory(nonExistingId, updateCategory));

    String expected = String.format("Category with id %s not found.", nonExistingId);
    assertEquals(expected, actual.getMessage());
    verify(repository, times(1)).findById(nonExistingId);
    verify(repository, times(0)).findByName(null);
    verify(repository, times(0)).save(null);
  }

  @Test
  void updateCategory_whenExistingName_shouldThrowAlreadyExistsException() {
    String id = "A";
    Category retrievedCategory = new Category();
    retrievedCategory.setId(id);
    retrievedCategory.setName("Old");
    when(repository.findById(id)).thenReturn(Optional.of(retrievedCategory));
    String existingName = "New";
    Category differentCategory = new Category();
    differentCategory.setId("B");
    differentCategory.setName(existingName);
    when(repository.findByName(existingName)).thenReturn(differentCategory);
    Category updateCategory = new Category();
    updateCategory.setId(id);
    updateCategory.setName(existingName);

    Exception actual = assertThrows(CategoryAlreadyExistsException.class,
        () -> service.updateCategory(id, updateCategory));

    String expected = String.format("Category with name %s already exists.", existingName);
    assertEquals(expected, actual.getMessage());
    verify(repository, times(1)).findById(id);
    verify(repository, times(1)).findByName(existingName);
    verify(repository, times(0)).save(null);
  }

  @Test
  void updateCategory_whenMismatchId_shouldThrowAlreadyExistsException() {
    String id = "A";
    Category retrievedCategory = new Category();
    retrievedCategory.setId(id);
    retrievedCategory.setName("Blog");
    when(repository.findById(id)).thenReturn(Optional.of(retrievedCategory));
    String differentId = "B";
    Category differentCategory = new Category();
    differentCategory.setId(differentId);
    differentCategory.setName("Tutorial");

    Exception actual = assertThrows(CategoryMismatchException.class,
        () -> service.updateCategory(id, differentCategory));

    String expected =
        String.format("Category with id %s does not match category argument %s.", id, differentId);
    assertEquals(expected, actual.getMessage());
    verify(repository, times(1)).findById(id);
    verify(repository, times(0)).findByName(null);
    verify(repository, times(0)).save(null);
  }

  @Test
  void deleteCategory_whenExistingId_shouldNotThrowException() throws CategoryNotFoundException {
    String existingId = "A";
    Category existingCategory = new Category();
    existingCategory.setId(existingId);
    existingCategory.setName("Blog");
    when(repository.findById(existingId)).thenReturn(Optional.of(existingCategory));

    service.deleteCategory(existingId);

    verify(repository, times(1)).findById(existingId);
    verify(repository, times(1)).deleteById(existingId);
  }

  @Test
  void deleteCategory_whenNonexistingId_shouldThrowNotFoundException() {
    String nonExistingId = "Z";
    Optional<Category> nonExistingCategory = Optional.empty();
    when(repository.findById(nonExistingId)).thenReturn(nonExistingCategory);

    Exception actual =
        assertThrows(CategoryNotFoundException.class, () -> service.deleteCategory(nonExistingId));

    String expected = String.format("Category with id %s not found.", nonExistingId);
    assertEquals(expected, actual.getMessage());
    verify(repository, times(1)).findById(nonExistingId);
    verify(repository, times(0)).deleteById(null);
  }
}
