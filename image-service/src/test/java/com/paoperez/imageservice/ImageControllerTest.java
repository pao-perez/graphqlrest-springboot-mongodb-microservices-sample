package com.paoperez.imageservice;

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
class ImageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ImageService service;

    @Test
    void getAllImages_shouldReturnOk() throws Exception {
        final Image imageA = Image.builder().id("A").name("imageA").url("/path/to/imageA").alt("Image A").width(150)
                .height(150).build();
        final Image imageB = Image.builder().id("B").name("imageB").url("/path/to/imageB").alt("Image B").width(150)
                .height(150).build();
        final Collection<Image> images = ImmutableList.of(imageA, imageB);
        when(service.getAllImages()).thenReturn(images);

        this.mockMvc.perform(get("/images").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(images)));

        verify(service, times(1)).getAllImages();
    }

    @Test
    void getImage_whenExistingId_shouldReturnOk() throws Exception {
        final String existingId = "A";
        final Image existingImage = Image.builder().id(existingId).name("imageA").url("/path/to/imageA").alt("Image A")
                .width(150).height(150).build();
        when(service.getImage(existingId)).thenReturn(existingImage);

        this.mockMvc.perform(get("/images/{id}", existingId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().string(objectMapper.writeValueAsString(existingImage)));

        verify(service, times(1)).getImage(existingId);
    }

    @Test
    void getImage_whenNonexistingId_shouldReturnNotFound() throws Exception {
        final String nonExistingId = "Z";
        when(service.getImage(nonExistingId)).thenThrow(new ImageNotFoundException(nonExistingId));

        this.mockMvc.perform(get("/images/{id}", nonExistingId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(String.format("Image with id %s not found.", nonExistingId)));

        verify(service, times(1)).getImage(nonExistingId);
    }

    @Test
    void getImage_whenBlankId_shouldReturnBadRequest() throws Exception {
        final String blankId = " ";

        this.mockMvc.perform(get("/images/{id}", blankId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value(containsString("must not be blank")));

        verify(service, times(0)).getImage(blankId);
    }

    @Test
    void createImage_whenNonexistingUrl_shouldReturnCreated() throws Exception {
        final String nonExistingUrl = "/path/to/new/image";
        final String name = "imageA";
        final String alt = "Image A";
        final Integer width = 150;
        final Integer height = 150;
        final Image newImage = Image.builder().name(name).url(nonExistingUrl).alt(alt).width(width).height(height)
                .build();
        final String createdId = "A";
        final Image createdImage = Image.builder().id(createdId).name(name).url(nonExistingUrl).alt(alt).width(width)
                .height(height).build();
        final String createdLocation = "http://localhost/images/" + createdId;
        when(service.createImage(newImage)).thenReturn(createdImage);

        this.mockMvc
                .perform(post("/images").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newImage)))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(createdImage)))
                .andExpect(header().string(LOCATION, createdLocation));

        verify(service, times(1)).createImage(newImage);
    }

    @Test
    void createImage_whenExistingUrl_shouldReturnConflict() throws Exception {
        final String existingUrl = "/path/to/existing/image";
        final Image newImage = Image.builder().name("imageA").url(existingUrl).alt("Image A").width(150).height(150)
                .build();
        when(service.createImage(newImage)).thenThrow(new ImageAlreadyExistsException(existingUrl));

        this.mockMvc
                .perform(post("/images").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newImage)))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.name()))
                .andExpect(
                        jsonPath("$.message").value(String.format("Image with url %s already exists.", existingUrl)));

        verify(service, times(1)).createImage(newImage);
    }

    @Test
    void createImage_whenBlankUrl_shouldReturnBadRequest() throws Exception {
        final Image blankImage = Image.builder().url(" ").build();

        this.mockMvc
                .perform(post("/images").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blankImage)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value(containsString("url must not be blank")));

        verify(service, times(0)).createImage(blankImage);
    }

    @Test
    void updateImage_whenExistingId_shouldReturnNoContent() throws Exception {
        final String existingId = "A";
        final Image updateImage = Image.builder().id(existingId).name("imageA").url("/path/to/updated/image")
                .alt("Image A").width(150).height(150).build();

        this.mockMvc.perform(put("/images/{id}", existingId).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateImage))).andExpect(status().isNoContent());

        verify(service, times(1)).updateImage(existingId, updateImage);
    }

    @Test
    void updateImage_whenExistingUrl_shouldReturnConflict() throws Exception {
        final String currentId = "A";
        final String existingUrl = "/path/to/existing/image";
        final Image currentImage = Image.builder().id(currentId).name("imageA").url(existingUrl).alt("Image A")
                .height(150).width(150).build();
        doThrow(new ImageAlreadyExistsException(existingUrl)).when(service).updateImage(currentId, currentImage);

        this.mockMvc
                .perform(put("/images/{id}", currentId).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(currentImage)))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.name()))
                .andExpect(
                        jsonPath("$.message").value(String.format("Image with url %s already exists.", existingUrl)));

        verify(service, times(1)).updateImage(currentId, currentImage);
    }

    @Test
    void updateImage_whenBlankUrl_shouldReturnBadRequest() throws Exception {
        final String currentId = "A";
        final Image blankImage = Image.builder().id(currentId).url(" ").build();

        this.mockMvc
                .perform(put("/images/{id}", currentId).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blankImage)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value(containsString("url must not be blank")));

        verify(service, times(0)).updateImage(currentId, blankImage);
    }

    @Test
    void updateImage_whenNonexistingId_shouldReturnNotFound() throws Exception {
        final String nonExistingId = "Z";
        final Image nonExistingImage = Image.builder().id(nonExistingId).name("imageA").url("/path/to/imageA")
                .alt("Image A").height(150).width(150).build();
        doThrow(new ImageNotFoundException(nonExistingId)).when(service).updateImage(nonExistingId, nonExistingImage);

        this.mockMvc
                .perform(put("/images/{id}", nonExistingId).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistingImage)))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(String.format("Image with id %s not found.", nonExistingId)));

        verify(service, times(1)).updateImage(nonExistingId, nonExistingImage);
    }

    @Test
    void deleteImage_whenExistingId_shouldReturnNoContent() throws Exception {
        final String existingId = "A";

        this.mockMvc.perform(delete("/images/{id}", existingId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteImage(existingId);
    }

    @Test
    void deleteImage_whenBlankId_shouldReturnBadRequest() throws Exception {
        final String blankId = " ";

        this.mockMvc.perform(delete("/images/{id}", blankId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value(containsString("must not be blank")));

        verify(service, times(0)).deleteImage(blankId);
    }

    @Test
    void deleteImage_whenNonexistingId_shouldReturnNotFound() throws Exception {
        final String nonExistingId = "Z";
        doThrow(new ImageNotFoundException(nonExistingId)).when(service).deleteImage(nonExistingId);

        this.mockMvc.perform(delete("/images/{id}", nonExistingId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(String.format("Image with id %s not found.", nonExistingId)));

        verify(service, times(1)).deleteImage(nonExistingId);
    }

}
