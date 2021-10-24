package com.paoperez.categoryservice;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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

  @MockBean
  private CategoryMapper categoryMapper;

  @Test
  void getAllCategories_shouldReturnOk() throws Exception {
    Category categoryA = new Category();
    categoryA.setId("A");
    categoryA.setName("Blog");
    Category categoryB = new Category();
    categoryA.setId("B");
    categoryA.setName("Tutorial");
    Collection<Category> categories = ImmutableList.of(categoryA, categoryB);
    when(service.getAllCategories()).thenReturn(categories);
    CategoryDTO categoryDtoA = new CategoryDTO();
    categoryDtoA.setId("A");
    categoryDtoA.setName("Blog");
    CategoryDTO categoryDtoB = new CategoryDTO();
    categoryDtoB.setId("B");
    categoryDtoB.setName("Tutorial");
    Collection<CategoryDTO> categoryDTOs = ImmutableList.of(categoryDtoA, categoryDtoB);
    when(categoryMapper.categoriesToCategoryDTOs(categories)).thenReturn(categoryDTOs);
    CategoriesDTO categoriesDto = CategoriesDTO.builder().data(categoryDTOs).build();

    this.mockMvc.perform(get("/categories").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(objectMapper.writeValueAsString(categoriesDto)));

    verify(service, times(1)).getAllCategories();
    verify(categoryMapper, times(1)).categoriesToCategoryDTOs(categories);
  }

  @Test
  void getCategory_whenExistingId_shouldReturnOk() throws Exception {
    String existingId = "A";
    Category existingCategory = new Category();
    existingCategory.setId("A");
    existingCategory.setName("Blog");
    when(service.getCategory(existingId)).thenReturn(existingCategory);
    CategoryDTO existingCategoryDto = new CategoryDTO();
    existingCategoryDto.setId("A");
    existingCategoryDto.setName("Blog");
    when(categoryMapper.categoryToCategoryDto(existingCategory)).thenReturn(existingCategoryDto);

    this.mockMvc
        .perform(get("/categories/{id}", existingId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(objectMapper.writeValueAsString(existingCategoryDto)));

    verify(service, times(1)).getCategory(existingId);
    verify(categoryMapper, times(1)).categoryToCategoryDto(existingCategory);
  }

  @Test
  void getCategory_whenNonexistingId_shouldReturnNotFound() throws Exception {
    String nonExistingId = "Z";
    when(service.getCategory(nonExistingId))
        .thenThrow(new CategoryNotFoundException(nonExistingId));

    this.mockMvc
        .perform(get("/categories/{id}", nonExistingId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
        .andExpect(jsonPath("$.message")
            .value(String.format("Category with id %s not found.", nonExistingId)));

    verify(service, times(1)).getCategory(nonExistingId);
    verify(categoryMapper, times(0)).categoryToCategoryDto(null);
  }

  @Test
  void getCategory_whenBlankId_shouldReturnBadRequest() throws Exception {
    String blankId = " ";

    this.mockMvc.perform(get("/categories/{id}", blankId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
        .andExpect(jsonPath("$.message").value(containsString("must not be blank")));

    verify(service, times(0)).getCategory(null);
    verify(categoryMapper, times(0)).categoryToCategoryDto(null);
  }

  @Test
  void createCategory_whenNonexistingName_shouldReturnCreated() throws Exception {
    String nonExistingName = "Blog";
    CategoryDTO categoryDto = new CategoryDTO();
    categoryDto.setName(nonExistingName);
    Category category = new Category();
    category.setName(nonExistingName);
    when(categoryMapper.categoryDtoToCategory(categoryDto)).thenReturn(category);
    String createdId = "A";
    String createdLocation = "http://localhost/categories/" + createdId;
    when(service.createCategory(category)).thenReturn(createdId);

    this.mockMvc
        .perform(post("/categories").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(categoryDto)))
        .andExpect(status().isCreated()).andExpect(content().string(createdId))
        .andExpect(header().string(LOCATION, createdLocation));

    verify(categoryMapper, times(1)).categoryDtoToCategory(categoryDto);
    verify(service, times(1)).createCategory(category);
  }

  @Test
  void createCategory_whenExistingName_shouldReturnConflict() throws Exception {
    final String existingName = "Blog";
    CategoryDTO categoryDto = new CategoryDTO();
    categoryDto.setName(existingName);
    Category category = new Category();
    category.setName(existingName);
    when(categoryMapper.categoryDtoToCategory(categoryDto)).thenReturn(category);
    when(service.createCategory(category))
        .thenThrow(new CategoryAlreadyExistsException(existingName));

    this.mockMvc
        .perform(post("/categories").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(categoryDto)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.name()))
        .andExpect(jsonPath("$.message")
            .value(String.format("Category with name %s already exists.", existingName)));

    verify(categoryMapper, times(1)).categoryDtoToCategory(categoryDto);
    verify(service, times(1)).createCategory(category);
  }

  @Test
  void createCategory_whenBlankName_shouldReturnBadRequest() throws Exception {
    CategoryDTO blankCategory = new CategoryDTO();

    this.mockMvc
        .perform(post("/categories").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(blankCategory)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
        .andExpect(jsonPath("$.message").value(containsString("name must not be blank")));

    verify(categoryMapper, times(0)).categoryDtoToCategory(null);
    verify(service, times(0)).createCategory(null);
  }

  @Test
  void updateCategory_whenExistingId_shouldReturnNoContent() throws Exception {
    String existingId = "A";
    CategoryDTO categoryDto = new CategoryDTO();
    categoryDto.setId(existingId);
    categoryDto.setName("Blog");
    Category category = new Category();
    category.setId(existingId);
    category.setName("Blog");
    when(categoryMapper.categoryDtoToCategory(categoryDto)).thenReturn(category);

    this.mockMvc
        .perform(put("/categories/{id}", existingId).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(categoryDto)))
        .andExpect(status().isNoContent());

    verify(categoryMapper, times(1)).categoryDtoToCategory(categoryDto);
    verify(service, times(1)).updateCategory(existingId, category);
  }

  @Test
  void updateCategory_whenExistingName_shouldReturnConflict() throws Exception {
    String existingName = "Blog";
    String id = "A";
    CategoryDTO categoryDto = new CategoryDTO();
    categoryDto.setId(id);
    categoryDto.setName(existingName);
    Category category = new Category();
    category.setId(id);
    category.setName(existingName);
    when(categoryMapper.categoryDtoToCategory(categoryDto)).thenReturn(category);
    doThrow(new CategoryAlreadyExistsException(existingName)).when(service).updateCategory(id,
        category);

    this.mockMvc
        .perform(put("/categories/{id}", id).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(categoryDto)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.name()))
        .andExpect(jsonPath("$.message")
            .value(String.format("Category with name %s already exists.", existingName)));

    verify(categoryMapper, times(1)).categoryDtoToCategory(categoryDto);
    verify(service, times(1)).updateCategory(id, category);
  }

  @Test
  void updateCategory_whenBlankName_shouldReturnBadRequest() throws Exception {
    String currentId = "A";
    CategoryDTO blankCategory = new CategoryDTO();

    this.mockMvc
        .perform(put("/categories/{id}", currentId).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(blankCategory)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
        .andExpect(jsonPath("$.message").value(containsString("name must not be blank")));

    verify(categoryMapper, times(0)).categoryDtoToCategory(null);
    verify(service, times(0)).updateCategory(null, null);
  }

  @Test
  void updateCategory_whenMismatchId_shouldReturnBadRequest() throws Exception {
    String differentId = "B";
    CategoryDTO categoryDto = new CategoryDTO();
    categoryDto.setId(differentId);
    categoryDto.setName("Blog");
    Category category = new Category();
    category.setId(differentId);
    category.setName("Blog");
    when(categoryMapper.categoryDtoToCategory(categoryDto)).thenReturn(category);
    String id = "A";
    doThrow(new CategoryMismatchException(id, differentId)).when(service).updateCategory(id,
        category);

    this.mockMvc
        .perform(put("/categories/{id}", id).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(categoryDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
        .andExpect(jsonPath("$.message").value(String
            .format("Category with id %s does not match category argument %s.", id, differentId)));

    verify(categoryMapper, times(1)).categoryDtoToCategory(categoryDto);
    verify(service, times(1)).updateCategory(id, category);
  }

  @Test
  void updateCategory_whenNonexistingId_shouldReturnNotFound() throws Exception {
    String nonExistingId = "Z";
    CategoryDTO categoryDto = new CategoryDTO();
    categoryDto.setId(nonExistingId);
    categoryDto.setName("Blog");
    Category category = new Category();
    category.setId(nonExistingId);
    category.setName("Blog");
    when(categoryMapper.categoryDtoToCategory(categoryDto)).thenReturn(category);
    doThrow(new CategoryNotFoundException(nonExistingId)).when(service)
        .updateCategory(nonExistingId, category);

    this.mockMvc
        .perform(put("/categories/{id}", nonExistingId).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(categoryDto)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
        .andExpect(jsonPath("$.message")
            .value(String.format("Category with id %s not found.", nonExistingId)));

    verify(categoryMapper, times(1)).categoryDtoToCategory(categoryDto);
    verify(service, times(1)).updateCategory(nonExistingId, category);
  }

  @Test
  void deleteCategory_whenExistingId_shouldReturnNoContent() throws Exception {
    final String existingId = "A";

    this.mockMvc
        .perform(delete("/categories/{id}", existingId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    verify(service, times(1)).deleteCategory(existingId);
  }

  @Test
  void deleteCategory_whenBlankId_shouldReturnBadRequest() throws Exception {
    final String blankId = " ";

    this.mockMvc
        .perform(delete("/categories/{id}", blankId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
        .andExpect(jsonPath("$.message").value(containsString("must not be blank")));

    verify(service, times(0)).deleteCategory(blankId);
  }

  @Test
  void deleteCategory_whenNonexistingId_shouldReturnNotFound() throws Exception {
    final String nonExistingId = "Z";
    doThrow(new CategoryNotFoundException(nonExistingId)).when(service)
        .deleteCategory(nonExistingId);

    this.mockMvc
        .perform(delete("/categories/{id}", nonExistingId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
        .andExpect(jsonPath("$.message")
            .value(String.format("Category with id %s not found.", nonExistingId)));

    verify(service, times(1)).deleteCategory(nonExistingId);
  }
}
