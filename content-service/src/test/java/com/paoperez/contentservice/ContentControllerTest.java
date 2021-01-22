package com.paoperez.contentservice;

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
import java.util.Date;
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
class ContentControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private ContentService service;

        @MockBean
        private ContentMapper contentMapper;

        @Test
        void getAllContents_shouldReturnOk() throws Exception {
                long created = new Date().toInstant().toEpochMilli();
                Content contentA = new Content();
                contentA.setAvatarId("avatarIdA");
                contentA.setCategoryId("categoryIdA");
                contentA.setImageId("imageIdA");
                contentA.setTitle("Blog A");
                contentA.setBody("Lorem ipsum dolor");
                contentA.setRank(1);
                contentA.setId("A");
                contentA.setCreated(created);
                Content contentB = new Content();
                contentB.setAvatarId("avatarIdB");
                contentB.setCategoryId("categoryIdB");
                contentB.setImageId("imageIdB");
                contentB.setTitle("Blog B");
                contentB.setBody("Ut enim ad minim veniam");
                contentB.setRank(2);
                contentB.setId("B");
                contentB.setCreated(created);
                Collection<Content> contents = ImmutableList.of(contentA, contentB);
                when(service.getAllContents()).thenReturn(contents);
                ContentDTO contentDtoA = new ContentDTO();
                contentDtoA.setAvatarId("avatarIdA");
                contentDtoA.setCategoryId("categoryIdA");
                contentDtoA.setImageId("imageIdA");
                contentDtoA.setTitle("Blog A");
                contentDtoA.setBody("Lorem ipsum dolor");
                contentDtoA.setRank(1);
                contentDtoA.setId("A");
                contentDtoA.setCreated(created);
                ContentDTO contentDtoB = new ContentDTO();
                contentDtoB.setAvatarId("avatarIdB");
                contentDtoB.setCategoryId("categoryIdB");
                contentDtoB.setImageId("imageIdB");
                contentDtoB.setTitle("Blog B");
                contentDtoB.setBody("Ut enim ad minim veniam");
                contentDtoB.setRank(2);
                contentDtoB.setId("B");
                contentDtoB.setCreated(created);
                Collection<ContentDTO> contentDTOs = ImmutableList.of(contentDtoA, contentDtoB);
                when(contentMapper.contentsToContentDTOs(contents)).thenReturn(contentDTOs);
                ContentsDTO contentsDto = ContentsDTO.builder().data(contentDTOs).build();

                this.mockMvc.perform(get("/contents").contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk()).andExpect(content().string(
                                                objectMapper.writeValueAsString(contentsDto)));

                verify(service, times(1)).getAllContents();
                verify(contentMapper, times(1)).contentsToContentDTOs(contents);
        }

        @Test
        void getContent_whenExistingId_shouldReturnOk() throws Exception {
                long created = new Date().toInstant().toEpochMilli();
                String existingId = "A";
                Content existingContent = new Content();
                existingContent.setAvatarId("avatarIdA");
                existingContent.setCategoryId("categoryIdA");
                existingContent.setImageId("imageIdA");
                existingContent.setTitle("Blog A");
                existingContent.setBody("Lorem ipsum dolor");
                existingContent.setRank(1);
                existingContent.setId(existingId);
                existingContent.setCreated(created);
                when(service.getContent(existingId)).thenReturn(existingContent);
                ContentDTO existingContentDto = new ContentDTO();
                existingContentDto.setAvatarId("avatarIdA");
                existingContentDto.setCategoryId("categoryIdA");
                existingContentDto.setImageId("imageIdA");
                existingContentDto.setTitle("Blog A");
                existingContentDto.setBody("Lorem ipsum dolor");
                existingContentDto.setRank(1);
                existingContentDto.setId(existingId);
                existingContentDto.setCreated(created);
                when(contentMapper.contentToContentDto(existingContent))
                                .thenReturn(existingContentDto);

                this.mockMvc.perform(get("/contents/{id}", existingId)
                                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                                .andExpect(content().string(objectMapper
                                                .writeValueAsString(existingContentDto)));

                verify(service, times(1)).getContent(existingId);
                verify(contentMapper, times(1)).contentToContentDto(existingContent);
        }

        @Test
        void getContent_whenNonexistingId_shouldReturnNotFound() throws Exception {
                String nonExistingId = "Z";
                when(service.getContent(nonExistingId))
                                .thenThrow(new ContentNotFoundException(nonExistingId));

                this.mockMvc.perform(get("/contents/{id}", nonExistingId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                                .andExpect(jsonPath("$.message").value(String.format(
                                                "Content with id %s not found.", nonExistingId)));

                verify(service, times(1)).getContent(nonExistingId);
                verify(contentMapper, times(0)).contentToContentDto(null);
        }

        @Test
        void getContent_whenBlankId_shouldReturnBadRequest() throws Exception {
                String blankId = " ";

                this.mockMvc.perform(get("/contents/{id}", blankId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status")
                                                .value(HttpStatus.BAD_REQUEST.name()))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("must not be blank")));

                verify(service, times(0)).getContent(blankId);
                verify(contentMapper, times(0)).contentToContentDto(null);
        }

        @Test
        void createContent_shouldReturnCreated() throws Exception {
                ContentDTO contentDto = new ContentDTO();
                contentDto.setAvatarId("avatarIdA");
                contentDto.setCategoryId("categoryIdA");
                contentDto.setImageId("imageIdA");
                contentDto.setTitle("Blog A");
                contentDto.setBody("Lorem ipsum dolor");
                contentDto.setRank(1);
                Content content = new Content();
                content.setAvatarId("avatarIdA");
                content.setCategoryId("categoryIdA");
                content.setImageId("imageIdA");
                content.setTitle("Blog A");
                content.setBody("Lorem ipsum dolor");
                content.setRank(1);
                when(contentMapper.contentDtoToContent(contentDto)).thenReturn(content);
                String createdId = "A";
                when(service.createContent(content)).thenReturn(createdId);
                String createdLocation = "http://localhost/contents/" + createdId;

                this.mockMvc.perform(post("/contents").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(contentDto)))
                                .andExpect(status().isCreated())
                                .andExpect(content().string(createdId))
                                .andExpect(header().string(LOCATION, createdLocation));

                verify(contentMapper, times(1)).contentDtoToContent(contentDto);
                verify(service, times(1)).createContent(content);
        }

        @Test
        void createContent_whenBlankFields_shouldReturnBadRequest() throws Exception {
                ContentDTO blankContent = new ContentDTO();

                this.mockMvc.perform(post("/contents").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(blankContent)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status")
                                                .value(HttpStatus.BAD_REQUEST.name()))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("imageId must not be blank")))
                                .andExpect(jsonPath("$.message").value(
                                                containsString("avatarId must not be blank")))
                                .andExpect(jsonPath("$.message").value(
                                                containsString("categoryId must not be blank")))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("title must not be blank")))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("body must not be blank")))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("rank must not be empty")));

                verify(contentMapper, times(0)).contentDtoToContent(null);
                verify(service, times(0)).createContent(null);
        }

        @Test
        void updateContent_whenExistingId_shouldReturnNoContent() throws Exception {
                String existingId = "A";
                long created = new Date().toInstant().toEpochMilli();
                ContentDTO contentDto = new ContentDTO();
                contentDto.setId(existingId);
                contentDto.setCreated(created);
                contentDto.setAvatarId("avatarIdA");
                contentDto.setCategoryId("categoryIdA");
                contentDto.setImageId("imageIdA");
                contentDto.setTitle("Blog A");
                contentDto.setBody("Lorem ipsum dolor");
                contentDto.setRank(1);
                Content content = new Content();
                content.setId(existingId);
                content.setCreated(created);
                content.setAvatarId("avatarIdA");
                content.setCategoryId("categoryIdA");
                content.setImageId("imageIdA");
                content.setTitle("Blog A");
                content.setBody("Lorem ipsum dolor");
                content.setRank(1);
                when(contentMapper.contentDtoToContent(contentDto)).thenReturn(content);

                this.mockMvc.perform(put("/contents/{id}", existingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(contentDto)))
                                .andExpect(status().isNoContent());

                verify(contentMapper, times(1)).contentDtoToContent(contentDto);
                verify(service, times(1)).updateContent(existingId, content);
        }

        @Test
        void updateContent_whenBlankFields_shouldReturnBadRequest() throws Exception {
                String currentId = "A";
                ContentDTO blankContent = new ContentDTO();
                blankContent.setId(currentId);
                blankContent.setRank(0);

                this.mockMvc.perform(put("/contents/{id}", currentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(blankContent)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status")
                                                .value(HttpStatus.BAD_REQUEST.name()))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("imageId must not be blank")))
                                .andExpect(jsonPath("$.message").value(
                                                containsString("avatarId must not be blank")))
                                .andExpect(jsonPath("$.message").value(
                                                containsString("categoryId must not be blank")))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("title must not be blank")))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("body must not be blank")))
                                .andExpect(jsonPath("$.message").value(
                                                containsString("rank must be a positive number")));

                verify(contentMapper, times(0)).contentDtoToContent(null);
                verify(service, times(0)).updateContent(null, null);
        }

        @Test
        void updateContent_whenMismatchId_shouldReturnBadRequest() throws Exception {
                long created = new Date().toInstant().toEpochMilli();
                String differentId = "differentId";
                ContentDTO contentDto = new ContentDTO();
                contentDto.setId(differentId);
                contentDto.setCreated(created);
                contentDto.setAvatarId("avatarId");
                contentDto.setCategoryId("categoryId");
                contentDto.setImageId("imageId");
                contentDto.setTitle("Blog A");
                contentDto.setBody("Lorem ipsum dolor");
                contentDto.setRank(1);
                Content content = new Content();
                content.setId(differentId);
                content.setCreated(created);
                content.setAvatarId("avatarIdA");
                content.setCategoryId("categoryIdA");
                content.setImageId("imageIdA");
                content.setTitle("Blog A");
                content.setBody("Lorem ipsum dolor");
                content.setRank(1);
                when(contentMapper.contentDtoToContent(contentDto)).thenReturn(content);
                String id = "A";
                doThrow(new ContentMismatchException(id, differentId)).when(service)
                                .updateContent(id, content);

                this.mockMvc.perform(
                                put("/contents/{id}", id).contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper
                                                                .writeValueAsString(contentDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status")
                                                .value(HttpStatus.BAD_REQUEST.name()))
                                .andExpect(jsonPath("$.message").value(String.format(
                                                "Content with id %s does not match content argument %s.",
                                                id, differentId)));

                verify(contentMapper, times(1)).contentDtoToContent(contentDto);
                verify(service, times(1)).updateContent(id, content);
        }

        @Test
        void updateContent_whenNonexistingId_shouldReturnNotFound() throws Exception {
                String nonExistingId = "Z";
                long created = new Date().toInstant().toEpochMilli();
                ContentDTO contentDto = new ContentDTO();
                contentDto.setId(nonExistingId);
                contentDto.setCreated(created);
                contentDto.setAvatarId("avatarId");
                contentDto.setCategoryId("categoryId");
                contentDto.setImageId("imageId");
                contentDto.setTitle("Blog A");
                contentDto.setBody("Lorem ipsum dolor");
                contentDto.setRank(1);
                Content content = new Content();
                content.setId(nonExistingId);
                content.setCreated(created);
                content.setAvatarId("avatarIdA");
                content.setCategoryId("categoryIdA");
                content.setImageId("imageIdA");
                content.setTitle("Blog A");
                content.setBody("Lorem ipsum dolor");
                content.setRank(1);
                when(contentMapper.contentDtoToContent(contentDto)).thenReturn(content);
                doThrow(new ContentNotFoundException(nonExistingId)).when(service)
                                .updateContent(nonExistingId, content);

                this.mockMvc.perform(put("/contents/{id}", nonExistingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(contentDto)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                                .andExpect(jsonPath("$.message").value(String.format(
                                                "Content with id %s not found.", nonExistingId)));

                verify(contentMapper, times(1)).contentDtoToContent(contentDto);
                verify(service, times(1)).updateContent(nonExistingId, content);
        }

        @Test
        void deleteContent_whenExistingId_shouldReturnNoContent() throws Exception {
                String existingId = "A";

                this.mockMvc.perform(delete("/contents/{id}", existingId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());

                verify(service, times(1)).deleteContent(existingId);
        }

        @Test
        void deleteContent_whenBlankId_shouldReturnBadRequest() throws Exception {
                String blankId = " ";

                this.mockMvc.perform(delete("/contents/{id}", blankId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status")
                                                .value(HttpStatus.BAD_REQUEST.name()))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("must not be blank")));

                verify(service, times(0)).deleteContent(blankId);
        }

        @Test
        void deleteContent_whenNonexistingId_shouldReturnNotFound() throws Exception {
                String nonExistingId = "Z";
                doThrow(new ContentNotFoundException(nonExistingId)).when(service)
                                .deleteContent(nonExistingId);

                this.mockMvc.perform(delete("/contents/{id}", nonExistingId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                                .andExpect(jsonPath("$.message").value(String.format(
                                                "Content with id %s not found.", nonExistingId)));

                verify(service, times(1)).deleteContent(nonExistingId);
        }
}
