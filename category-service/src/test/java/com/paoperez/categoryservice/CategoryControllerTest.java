package com.paoperez.categoryservice;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.hamcrest.Matchers.containsString;

import java.util.Collection;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService service;

    @Test
    void getAllCategories_shouldReturnOk() throws Exception {
        final Category categoryA = Category.builder().id("A").name("Blog").build();
        final Category categoryB = Category.builder().id("B").name("Tutorial").build();
        final Collection<Category> categories = ImmutableList.of(categoryA, categoryB);
        when(service.getAllCategories()).thenReturn(categories);

        this.mockMvc.perform(get("/categories").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(categories)));

        verify(service, times(1)).getAllCategories();
    }

    @Test
    void getCategory_whenExistingId_shouldReturnOk() throws Exception {
        final String existingId = "A";
        final Category existingCategory = Category.builder().id(existingId).name("Blog").build();
        when(service.getCategory(existingId)).thenReturn(existingCategory);

        this.mockMvc.perform(get("/categories/{id}", existingId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(existingCategory)));

        verify(service, times(1)).getCategory(existingId);
    }

    @Test
    void getCategory_whenNonexistingId_shouldReturnNotFound() throws Exception {
        final String nonExistingId = "Z";
        when(service.getCategory(nonExistingId)).thenThrow(new CategoryNotFoundException(nonExistingId));

        this.mockMvc.perform(get("/categories/{id}", nonExistingId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(String.format("Category with id %s not found.", nonExistingId)));

        verify(service, times(1)).getCategory(nonExistingId);
    }

    @Test
    void getCategory_whenBlankId_shouldReturnBadRequest() throws Exception {
        final String blankId = " ";

        this.mockMvc.perform(get("/categories/{id}", blankId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value(containsString("must not be blank")));

        verify(service, times(0)).getCategory(blankId);
    }

    @Test
    void createCategory_whenNonexistingName_shouldReturnCreated() throws Exception {
        final String nonExistingName = "Blog";
        final Category newCategory = Category.builder().name(nonExistingName).build();
        final String createdId = "A";
        final Category createdCategory = Category.builder().id(createdId).name(nonExistingName).build();
        final String createdLocation = "http://localhost/categories/" + createdId;
        when(service.createCategory(newCategory)).thenReturn(createdCategory);

        this.mockMvc
                .perform(post("/categories").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(createdCategory)))
                .andExpect(header().string(LOCATION, createdLocation));

        verify(service, times(1)).createCategory(newCategory);
    }

    @Test
    void createCategory_whenExistingName_shouldReturnConflict() throws Exception {
        final String existingName = "Blog";
        final Category newCategory = Category.builder().name(existingName).build();
        when(service.createCategory(newCategory)).thenThrow(new CategoryAlreadyExistsException(existingName));

        this.mockMvc
                .perform(post("/categories").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.name()))
                .andExpect(jsonPath("$.message")
                        .value(String.format("Category with name %s already exists.", existingName)));

        verify(service, times(1)).createCategory(newCategory);
    }

    @Test
    void createCategory_whenBlankName_shouldReturnBadRequest() throws Exception {
        final Category blankCategory = Category.builder().name(" ").build();

        this.mockMvc
                .perform(post("/categories").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blankCategory)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value(containsString("name must not be blank")));

        verify(service, times(0)).createCategory(blankCategory);
    }

    @Test
    void updateCategory_whenExistingId_shouldReturnNoContent() throws Exception {
        final String existingId = "A";
        final Category updatedCategory = Category.builder().id(existingId).name("Blog").build();

        this.mockMvc.perform(put("/categories/{id}", existingId).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCategory))).andExpect(status().isNoContent());

        verify(service, times(1)).updateCategory(existingId, updatedCategory);
    }

    @Test
    void updateCategory_whenExistingName_shouldReturnConflict() throws Exception {
        final String existingId = "A";
        final String existingName = "Blog";
        final Category existingCategory = Category.builder().id(existingId).name(existingName).build();
        doThrow(new CategoryAlreadyExistsException(existingName)).when(service).updateCategory(existingId,
                existingCategory);

        this.mockMvc
                .perform(put("/categories/{id}", existingId).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingCategory)))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.name()))
                .andExpect(jsonPath("$.message")
                        .value(String.format("Category with name %s already exists.", existingName)));

        verify(service, times(1)).updateCategory(existingId, existingCategory);
    }

    @Test
    void updateCategory_whenBlankName_shouldReturnBadRequest() throws Exception {
        final String existingId = "A";
        final Category blankCategory = Category.builder().id(existingId).name(" ").build();

        this.mockMvc
                .perform(put("/categories/{id}", existingId).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blankCategory)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value(containsString("name must not be blank")));

        verify(service, times(0)).updateCategory(existingId, blankCategory);
    }

    @Test
    void updateCategory_whenNonexistingId_shouldReturnNotFound() throws Exception {
        final String nonExistingId = "Z";
        final Category nonExistingCategory = Category.builder().id(nonExistingId).name("Blog").build();
        doThrow(new CategoryNotFoundException(nonExistingId)).when(service).updateCategory(nonExistingId,
                nonExistingCategory);

        this.mockMvc
                .perform(put("/categories/{id}", nonExistingId).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistingCategory)))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(String.format("Category with id %s not found.", nonExistingId)));

        verify(service, times(1)).updateCategory(nonExistingId, nonExistingCategory);
    }

    @Test
    void deleteCategory_whenExistingId_shouldReturnNoContent() throws Exception {
        final String existingId = "A";

        this.mockMvc.perform(delete("/categories/{id}", existingId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteCategory(existingId);
    }

    @Test
    void deleteCategory_whenBlankId_shouldReturnBadRequest() throws Exception {
        final String blankId = " ";

        this.mockMvc.perform(delete("/categories/{id}", blankId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value(containsString("must not be blank")));

        verify(service, times(0)).deleteCategory(blankId);
    }

    @Test
    void deleteCategory_whenNonexistingId_shouldReturnNotFound() throws Exception {
        final String nonExistingId = "Z";
        doThrow(new CategoryNotFoundException(nonExistingId)).when(service).deleteCategory(nonExistingId);

        this.mockMvc.perform(delete("/categories/{id}", nonExistingId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(String.format("Category with id %s not found.", nonExistingId)));

        verify(service, times(1)).deleteCategory(nonExistingId);
    }

}
