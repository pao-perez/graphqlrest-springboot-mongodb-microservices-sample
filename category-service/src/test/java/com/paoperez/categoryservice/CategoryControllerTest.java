package com.paoperez.categoryservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
@WebMvcTest
public class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService service;

    @Test
    public void validGetAllCategoriesShouldReturnOkResponse() throws Exception {
        final List<Category> categories = ImmutableList.of(new Category("A", "Blog"), new Category("B", "Tutorial"));
        when(service.getAllCategories()).thenReturn(categories);

        final String categoryContents = objectMapper.writeValueAsString(categories);
        MvcResult result = this.mockMvc.perform(get("/categories")).andReturn();

        verify(service, times(1)).getAllCategories();

        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(categoryContents, response.getContentAsString());
    }

    @Test
    public void validGetCategoryShouldReturnOkResponse() throws Exception {
        final String id = "A";
        final String name = "Blog";
        final Category category = new Category(id, name);
        when(service.getCategory(id)).thenReturn(category);

        final String categoryContent = objectMapper.writeValueAsString(category);
        MvcResult result = this.mockMvc.perform(get("/categories/{id}", id)).andReturn();

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(service, times(1)).getCategory(captor.capture());
        assertEquals(id, captor.getValue());

        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(categoryContent, response.getContentAsString());
    }

    @Test
    public void validCreateCategoryShouldReturnCreatedResponse() throws Exception {
        final String id = "C";
        final String name = "Content";
        final Category category = new Category(id, name);
        when(service.createCategory(category)).thenReturn(category);

        final String categoryContent = objectMapper.writeValueAsString(category);
        MvcResult result = this.mockMvc
                .perform(post("/categories").contentType(MediaType.APPLICATION_JSON).content(categoryContent))
                .andReturn();

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(service, times(1)).createCategory(captor.capture());
        assertEquals(id, captor.getValue().getId());
        assertEquals(name, captor.getValue().getName());

        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(categoryContent, response.getContentAsString());
    }

    @Test
    public void validUpdateCategoryShouldReturnNoContentResponse() throws Exception {
        final String id = "C";
        final String name = "Content";
        final Category category = new Category(id, name);
        when(service.updateCategory(category)).thenReturn(true);

        final String categoryContent = objectMapper.writeValueAsString(category);
        MvcResult result = this.mockMvc
                .perform(put("/categories").contentType(MediaType.APPLICATION_JSON).content(categoryContent))
                .andReturn();

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(service, times(1)).updateCategory(captor.capture());
        assertEquals(id, captor.getValue().getId());
        assertEquals(name, captor.getValue().getName());

        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @Test
    public void validDeleteCategoryShouldReturnNoContentResponse() throws Exception {
        final String id = "C";
        when(service.deleteCategory(id)).thenReturn(true);

        MvcResult result = this.mockMvc.perform(delete("/categories/{id}", id)).andReturn();

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(service, times(1)).deleteCategory(captor.capture());
        assertEquals(captor.getValue(), id);

        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

}
