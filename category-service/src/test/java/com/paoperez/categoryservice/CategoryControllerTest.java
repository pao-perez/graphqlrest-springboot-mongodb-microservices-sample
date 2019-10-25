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
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest
public class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService service;

    @Test
    public void getAllCategoriesShouldReturnOk() throws Exception {
        final Category validCategory = Category.builder().id("A").name("Blog").build();
        final Category otherCategory = Category.builder().id("B").name("Tutorial").build();
        final Collection<Category> categories = ImmutableList.of(validCategory, otherCategory);
        when(service.getAllCategories()).thenReturn(categories);

        this.mockMvc.perform(get("/categories").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(categories)));

        verify(service, times(1)).getAllCategories();
    }

    @Test
    public void getValidCategoryShouldReturnOk() throws Exception {
        final String validId = "A";
        final Category validCategory = Category.builder().id(validId).name("Blog").build();
        when(service.getCategory(validId)).thenReturn(validCategory);

        this.mockMvc.perform(get("/categories/{id}", validId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().string(objectMapper.writeValueAsString(validCategory)));

        verify(service, times(1)).getCategory(validId);
    }

    @Test
    public void getNonexistingCategoryIdShouldReturnNotFound() throws Exception {
        final String nonExistingId = "Z";
        when(service.getCategory(nonExistingId)).thenThrow(new CategoryNotFoundException(nonExistingId));

        this.mockMvc.perform(get("/categories/{id}", nonExistingId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(String.format("Category with id %s not found.", nonExistingId)));

        verify(service, times(1)).getCategory(nonExistingId);
    }

    @Test
    public void getBlankCategoryIdShouldReturnBadRequest() throws Exception {
        final String blankId = " ";

        this.mockMvc.perform(get("/categories/{id}", blankId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value(containsString("must not be blank")));

        verify(service, times(0)).getCategory(blankId);
    }

    @Test
    public void createValidCategoryShouldReturnCreated() throws Exception {
        final String validId = "A";
        final String validName = "Blog";
        final Category newCategory = Category.builder().name(validName).build();
        final Category createdCategory = Category.builder().id(validId).name(validName).build();
        final String createdLocation = "http://localhost/categories/" + validId;
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
    public void createExistingCategoryShouldReturnConflict() throws Exception {
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
    public void createBlankCategoryShouldReturnBadRequest() throws Exception {
        final Category blankCategory = Category.builder().name(" ").build();

        this.mockMvc
                .perform(post("/categories").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blankCategory)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value(containsString("name must not be blank")));

        verify(service, times(0)).createCategory(blankCategory);
    }

    @Test
    public void updateValidCategoryShouldReturnNoContent() throws Exception {
        final String validId = "A";
        final Category validCategory = Category.builder().id(validId).name("Blog").build();

        this.mockMvc.perform(put("/categories/{id}", validId).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCategory))).andExpect(status().isNoContent());

        verify(service, times(1)).updateCategory(validId, validCategory);
    }

    @Test
    public void updateBlankCategoryShouldReturnBadRequest() throws Exception {
        final String validId = "A";
        final Category blankCategory = Category.builder().name(" ").build();

        this.mockMvc
                .perform(put("/categories/{id}", validId).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blankCategory)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value(containsString("name must not be blank")));

        verify(service, times(0)).updateCategory(validId, blankCategory);
    }

    @Test
    public void updateNonexistingCategoryShouldReturnNotFound() throws Exception {
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
    public void deleteValidCategoryShouldReturnNoContent() throws Exception {
        final String validId = "A";

        this.mockMvc.perform(delete("/categories/{id}", validId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteCategory(validId);
    }

    @Test
    public void deleteNonexistingCategoryShouldReturnNotFound() throws Exception {
        final String nonExistingId = "Z";
        doThrow(new CategoryNotFoundException(nonExistingId)).when(service).deleteCategory(nonExistingId);

        this.mockMvc.perform(delete("/categories/{id}", nonExistingId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(String.format("Category with id %s not found.", nonExistingId)));

        verify(service, times(1)).deleteCategory(nonExistingId);
    }

    @Test
    public void deleteBlankCategoryShouldReturnBadRequest() throws Exception {
        final String blankId = " ";

        this.mockMvc.perform(delete("/categories/{id}", blankId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value(containsString("must not be blank")));

        verify(service, times(0)).deleteCategory(blankId);
    }
}
