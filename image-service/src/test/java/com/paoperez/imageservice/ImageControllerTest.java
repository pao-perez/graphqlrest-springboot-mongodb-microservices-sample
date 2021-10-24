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

        @MockBean
        private ImageMapper imageMapper;

        @Test
        void getAllImages_shouldReturnOk() throws Exception {
                Image imageA = new Image();
                imageA.setName("imageA");
                imageA.setUrl("/path/to/imageA");
                imageA.setAlt("image A");
                imageA.setWidth(150);
                imageA.setHeight(150);
                imageA.setId("A");
                Image imageB = new Image();
                imageB.setName("imageB");
                imageB.setUrl("/path/to/imageB");
                imageB.setAlt("image B");
                imageB.setWidth(150);
                imageB.setHeight(150);
                imageB.setId("B");
                Collection<Image> images = ImmutableList.of(imageA, imageB);
                when(service.getAllImages()).thenReturn(images);
                ImageDTO imageDtoA = new ImageDTO();
                imageDtoA.setName("imageA");
                imageDtoA.setUrl("/path/to/imageA");
                imageDtoA.setAlt("image A");
                imageDtoA.setWidth(150);
                imageDtoA.setHeight(150);
                imageDtoA.setId("A");
                ImageDTO imageDtoB = new ImageDTO();
                imageDtoB.setUrl("/path/to/imageB");
                imageDtoB.setAlt("image B");
                imageDtoB.setName("imageB");
                imageDtoB.setWidth(150);
                imageDtoB.setHeight(150);
                imageDtoB.setId("B");
                Collection<ImageDTO> imageDTOs = ImmutableList.of(imageDtoA, imageDtoB);
                when(imageMapper.imagesToImageDTOs(images)).thenReturn(imageDTOs);
                ImagesDTO imagesDto = ImagesDTO.builder().data(imageDTOs).build();

                this.mockMvc.perform(get("/images").contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk()).andExpect(content().string(
                                                objectMapper.writeValueAsString(imagesDto)));

                verify(service, times(1)).getAllImages();
                verify(imageMapper, times(1)).imagesToImageDTOs(images);
        }

        @Test
        void getImage_whenExistingId_shouldReturnOk() throws Exception {
                String existingId = "A";
                Image existingImage = new Image();
                existingImage.setName("imageA");
                existingImage.setUrl("/path/to/imageA");
                existingImage.setAlt("image A");
                existingImage.setWidth(150);
                existingImage.setHeight(150);
                existingImage.setId(existingId);
                when(service.getImage(existingId)).thenReturn(existingImage);
                ImageDTO existingImageDto = new ImageDTO();
                existingImageDto.setName("imageA");
                existingImageDto.setUrl("/path/to/imageA");
                existingImageDto.setAlt("image A");
                existingImageDto.setWidth(150);
                existingImageDto.setHeight(150);
                existingImageDto.setId(existingId);
                when(imageMapper.imageToImageDto(existingImage)).thenReturn(existingImageDto);

                this.mockMvc.perform(get("/images/{id}", existingId)
                                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                                .andExpect(content().string(
                                                objectMapper.writeValueAsString(existingImageDto)));

                verify(service, times(1)).getImage(existingId);
                verify(imageMapper, times(1)).imageToImageDto(existingImage);
        }

        @Test
        void getImage_whenNonexistingId_shouldReturnNotFound() throws Exception {
                String nonExistingId = "Z";
                when(service.getImage(nonExistingId))
                                .thenThrow(new ImageNotFoundException(nonExistingId));

                this.mockMvc.perform(get("/images/{id}", nonExistingId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                                .andExpect(jsonPath("$.message").value(String.format(
                                                "Image with id %s not found.", nonExistingId)));

                verify(service, times(1)).getImage(nonExistingId);
                verify(imageMapper, times(0)).imageToImageDto(null);
        }

        @Test
        void getImage_whenBlankId_shouldReturnBadRequest() throws Exception {
                String blankId = " ";

                this.mockMvc.perform(get("/images/{id}", blankId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status")
                                                .value(HttpStatus.BAD_REQUEST.name()))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("must not be blank")));

                verify(service, times(0)).getImage(blankId);
                verify(imageMapper, times(0)).imageToImageDto(null);
        }

        @Test
        void createImage_whenNonexistingUrl_shouldReturnCreated() throws Exception {
                String nonExistingUrl = "/path/to/new/image";
                ImageDTO imageDto = new ImageDTO();
                imageDto.setName("imageA");
                imageDto.setUrl(nonExistingUrl);
                imageDto.setAlt("image A");
                imageDto.setWidth(150);
                imageDto.setHeight(150);
                Image image = new Image();
                image.setName("imageA");
                image.setUrl(nonExistingUrl);
                image.setAlt("image A");
                image.setWidth(150);
                image.setHeight(150);
                when(imageMapper.imageDtoToImage(imageDto)).thenReturn(image);
                String createdId = "A";
                String createdLocation = "http://localhost/images/" + createdId;
                when(service.createImage(image)).thenReturn(createdId);

                this.mockMvc.perform(post("/images").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(imageDto)))
                                .andExpect(status().isCreated())
                                .andExpect(content().string(createdId))
                                .andExpect(header().string(LOCATION, createdLocation));

                verify(imageMapper, times(1)).imageDtoToImage(imageDto);
                verify(service, times(1)).createImage(image);
        }

        @Test
        void createImage_whenExistingUrl_shouldReturnConflict() throws Exception {
                String existingUrl = "/path/to/existing/image";
                ImageDTO imageDto = new ImageDTO();
                imageDto.setName("imageA");
                imageDto.setUrl(existingUrl);
                imageDto.setAlt("image A");
                imageDto.setWidth(150);
                imageDto.setHeight(150);
                Image image = new Image();
                image.setName("imageA");
                image.setUrl(existingUrl);
                image.setAlt("image A");
                image.setWidth(150);
                image.setHeight(150);
                when(imageMapper.imageDtoToImage(imageDto)).thenReturn(image);
                when(service.createImage(image))
                                .thenThrow(new ImageAlreadyExistsException(existingUrl));

                this.mockMvc.perform(post("/images").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(imageDto)))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.name()))
                                .andExpect(jsonPath("$.message").value(String.format(
                                                "Image with url %s already exists.", existingUrl)));

                verify(imageMapper, times(1)).imageDtoToImage(imageDto);
                verify(service, times(1)).createImage(image);
        }

        @Test
        void createImage_whenBlankFields_shouldReturnBadRequest() throws Exception {
                ImageDTO blankImage = new ImageDTO();

                this.mockMvc.perform(post("/images").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(blankImage)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status")
                                                .value(HttpStatus.BAD_REQUEST.name()))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("name must not be blank")))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("alt must not be blank")))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("url must not be blank")))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("height must not be blank")))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("width must not be blank")));

                verify(imageMapper, times(0)).imageDtoToImage(null);
                verify(service, times(0)).createImage(null);
        }

        @Test
        void updateImage_whenExistingId_shouldReturnNoContent() throws Exception {
                String existingId = "A";
                ImageDTO imageDto = new ImageDTO();
                imageDto.setName("imageA");
                imageDto.setUrl("/path/to/old/image");
                imageDto.setAlt("image A");
                imageDto.setWidth(150);
                imageDto.setHeight(150);
                imageDto.setId(existingId);
                Image image = new Image();
                image.setName("imageA");
                image.setUrl("/path/to/old/image");
                image.setAlt("image A");
                image.setWidth(150);
                image.setHeight(150);
                image.setId(existingId);
                when(imageMapper.imageDtoToImage(imageDto)).thenReturn(image);

                this.mockMvc.perform(put("/images/{id}", existingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(imageDto)))
                                .andExpect(status().isNoContent());

                verify(imageMapper, times(1)).imageDtoToImage(imageDto);
                verify(service, times(1)).updateImage(existingId, image);
        }

        @Test
        void updateImage_whenExistingUrl_shouldReturnConflict() throws Exception {
                String id = "A";
                String existingUrl = "/path/to/existing/image";
                ImageDTO imageDto = new ImageDTO();
                imageDto.setName("imageA");
                imageDto.setUrl(existingUrl);
                imageDto.setAlt("image A");
                imageDto.setWidth(150);
                imageDto.setHeight(150);
                imageDto.setId(id);
                Image image = new Image();
                image.setName("imageA");
                image.setUrl(existingUrl);
                image.setAlt("image A");
                image.setWidth(150);
                image.setHeight(150);
                image.setId(id);
                when(imageMapper.imageDtoToImage(imageDto)).thenReturn(image);
                doThrow(new ImageAlreadyExistsException(existingUrl)).when(service).updateImage(id,
                                image);

                this.mockMvc.perform(put("/images/{id}", id).contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(imageDto)))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.name()))
                                .andExpect(jsonPath("$.message").value(String.format(
                                                "Image with url %s already exists.", existingUrl)));

                verify(imageMapper, times(1)).imageDtoToImage(imageDto);
                verify(service, times(1)).updateImage(id, image);
        }

        @Test
        void updateImage_whenBlankFields_shouldReturnBadRequest() throws Exception {
                String id = "A";
                ImageDTO blankImage = new ImageDTO();
                blankImage.setWidth(0);
                blankImage.setHeight(-1);
                blankImage.setId(id);

                this.mockMvc.perform(put("/images/{id}", id).contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(blankImage)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status")
                                                .value(HttpStatus.BAD_REQUEST.name()))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("name must not be blank")))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("alt must not be blank")))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("url must not be blank")))
                                .andExpect(jsonPath("$.message").value(
                                                containsString("width must be a positive number")))
                                .andExpect(jsonPath("$.message").value(containsString(
                                                "height must be a positive number")));

                verify(imageMapper, times(0)).imageDtoToImage(null);
                verify(service, times(0)).updateImage(null, null);
        }

        @Test
        void updateImage_whenMismatchId_shouldReturnBadRequest() throws Exception {
                String differentId = "B";
                ImageDTO imageDto = new ImageDTO();
                imageDto.setName("imageA");
                imageDto.setUrl("/path/to/image");
                imageDto.setAlt("image A");
                imageDto.setWidth(150);
                imageDto.setHeight(150);
                imageDto.setId(differentId);
                Image image = new Image();
                image.setName("imageA");
                image.setUrl("/path/to/image");
                image.setAlt("image A");
                image.setWidth(150);
                image.setHeight(150);
                image.setId(differentId);
                when(imageMapper.imageDtoToImage(imageDto)).thenReturn(image);
                String id = "A";
                doThrow(new ImageMismatchException(id, differentId)).when(service).updateImage(id,
                                image);

                this.mockMvc.perform(put("/images/{id}", id).contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(imageDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status")
                                                .value(HttpStatus.BAD_REQUEST.name()))
                                .andExpect(jsonPath("$.message").value(String.format(
                                                "Image with id %s does not match image argument %s.",
                                                id, differentId)));

                verify(imageMapper, times(1)).imageDtoToImage(imageDto);
                verify(service, times(1)).updateImage(id, image);
        }

        @Test
        void updateImage_whenNonexistingId_shouldReturnNotFound() throws Exception {
                String nonExistingId = "Z";
                ImageDTO imageDto = new ImageDTO();
                imageDto.setName("imageA");
                imageDto.setUrl("/path/to/image");
                imageDto.setAlt("image A");
                imageDto.setWidth(150);
                imageDto.setHeight(150);
                imageDto.setId(nonExistingId);
                Image image = new Image();
                image.setName("imageA");
                image.setUrl("/path/to/image");
                image.setAlt("image A");
                image.setWidth(150);
                image.setHeight(150);
                image.setId(nonExistingId);
                when(imageMapper.imageDtoToImage(imageDto)).thenReturn(image);
                doThrow(new ImageNotFoundException(nonExistingId)).when(service)
                                .updateImage(nonExistingId, image);

                this.mockMvc.perform(put("/images/{id}", nonExistingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(imageDto)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                                .andExpect(jsonPath("$.message").value(String.format(
                                                "Image with id %s not found.", nonExistingId)));

                verify(imageMapper, times(1)).imageDtoToImage(imageDto);
                verify(service, times(1)).updateImage(nonExistingId, image);
        }

        @Test
        void deleteImage_whenExistingId_shouldReturnNoContent() throws Exception {
                String existingId = "A";

                this.mockMvc.perform(delete("/images/{id}", existingId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());

                verify(service, times(1)).deleteImage(existingId);
        }

        @Test
        void deleteImage_whenBlankId_shouldReturnBadRequest() throws Exception {
                String blankId = " ";

                this.mockMvc.perform(delete("/images/{id}", blankId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status")
                                                .value(HttpStatus.BAD_REQUEST.name()))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("must not be blank")));

                verify(service, times(0)).deleteImage(blankId);
        }

        @Test
        void deleteImage_whenNonexistingId_shouldReturnNotFound() throws Exception {
                String nonExistingId = "Z";
                doThrow(new ImageNotFoundException(nonExistingId)).when(service)
                                .deleteImage(nonExistingId);

                this.mockMvc.perform(delete("/images/{id}", nonExistingId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                                .andExpect(jsonPath("$.message").value(String.format(
                                                "Image with id %s not found.", nonExistingId)));

                verify(service, times(1)).deleteImage(nonExistingId);
        }
}
