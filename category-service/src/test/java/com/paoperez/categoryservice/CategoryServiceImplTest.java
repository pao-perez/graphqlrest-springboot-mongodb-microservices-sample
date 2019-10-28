package com.paoperez.categoryservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

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
        final Category categoryA = Category.builder().id("A").name("Blog").build();
        final Category categoryB = Category.builder().id("B").name("Tutorial").build();
        final List<Category> expected = ImmutableList.of(categoryA, categoryB);
        when(repository.findAll()).thenReturn(expected);

        Collection<Category> actual = service.getAllCategories();

        verify(repository, times(1)).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getCategory_whenExistingId_shouldReturnCategory() {
        final String existingId = "A";
        final Optional<Category> expected = Optional.of(Category.builder().id(existingId).name("Blog").build());
        when(repository.findById(existingId)).thenReturn(expected);

        Category actual = service.getCategory(existingId);

        assertEquals(expected.get(), actual);
        verify(repository, times(1)).findById(existingId);
    }

    @Test
    void getCategory_whenNonexistingId_shouldThrowNotFoundException() {
        final String nonExistingId = "Z";
        // final Optional<Category> nonExistingCategory = Optional.empty();
        // when(repository.findById(nonExistingId)).thenReturn(nonExistingCategory);

        Exception actual = assertThrows(CategoryNotFoundException.class, () -> service.getCategory(nonExistingId));

        String expected = String.format("Category with id %s not found.", nonExistingId);
        assertEquals(expected, actual.getMessage());
        verify(repository, times(1)).findById(nonExistingId);
    }

    @Test
    void createCategory_whenValidCategory_shouldReturnCategory() {
        final String validName = "Blog";
        when(repository.findByName(validName)).thenReturn(null);
        final Category newCategory = Category.builder().name(validName).build();
        final Category expected = Category.builder().id("A").name(validName).build();
        when(repository.save(newCategory)).thenReturn(expected);

        Category actual = service.createCategory(newCategory);

        assertEquals(expected, actual);
        verify(repository, times(1)).findByName(validName);
        verify(repository, times(1)).save(newCategory);
    }

    @Test
    void createCategory_whenExistingName_shouldThrowAlreadyExistsException() {
        final String existingName = "Blog";
        final Category existingCategory = Category.builder().id("A").name(existingName).build();
        when(repository.findByName(existingName)).thenReturn(existingCategory);

        final Category newCategory = Category.builder().name(existingName).build();
        Exception actual = assertThrows(CategoryAlreadyExistsException.class,
                () -> service.createCategory(newCategory));

        String expected = String.format("Category with name %s already exists.", existingName);
        assertEquals(expected, actual.getMessage());
        verify(repository, times(1)).findByName(existingName);
        verify(repository, times(0)).save(newCategory);
    }

    @Test
    void updateCategory_whenValidIdAndCategory_shouldNotThrowException() {
        final String existingId = "A";
        final Optional<Category> existingCategory = Optional.of(Category.builder().id(existingId).name("Old").build());
        when(repository.findById(existingId)).thenReturn(existingCategory);
        final String updatedName = "New";
        when(repository.findByName(updatedName)).thenReturn(null);

        final Category updatedCategory = Category.builder().id(existingId).name(updatedName).build();
        service.updateCategory(existingId, updatedCategory);

        verify(repository, times(1)).findById(existingId);
        verify(repository, times(1)).findByName(updatedName);
        verify(repository, times(1)).save(updatedCategory);
    }

    @Test
    void updateCategory_whenNonexistingId_shouldThrowNotFoundException() {
        final String nonExistingId = "A";
        final Optional<Category> nonExistingCategory = Optional.empty();
        when(repository.findById(nonExistingId)).thenReturn(nonExistingCategory);

        final String updatedName = "New";
        final Category updatedCategory = Category.builder().id(nonExistingId).name(updatedName).build();
        Exception actual = assertThrows(CategoryNotFoundException.class,
                () -> service.updateCategory(nonExistingId, updatedCategory));

        String expected = String.format("Category with id %s not found.", nonExistingId);
        assertEquals(expected, actual.getMessage());
        verify(repository, times(1)).findById(nonExistingId);
        verify(repository, times(0)).findByName(updatedName);
        verify(repository, times(0)).save(updatedCategory);
    }

    @Test
    void updateCategory_whenExistingName_shouldThrowAlreadyExistsException() {
        final String existingId = "A";
        final Optional<Category> existingCategory = Optional.of(Category.builder().id(existingId).name("Old").build());
        when(repository.findById(existingId)).thenReturn(existingCategory);
        final String existingName = "New";
        final Category foundCategory = Category.builder().id("B").name(existingName).build();
        when(repository.findByName(existingName)).thenReturn(foundCategory);

        final Category updatedCategory = Category.builder().id(existingId).name(existingName).build();
        Exception actual = assertThrows(CategoryAlreadyExistsException.class,
                () -> service.updateCategory(existingId, updatedCategory));

        String expected = String.format("Category with name %s already exists.", existingName);
        assertEquals(expected, actual.getMessage());
        verify(repository, times(1)).findById(existingId);
        verify(repository, times(1)).findByName(existingName);
        verify(repository, times(0)).save(updatedCategory);
    }

}
