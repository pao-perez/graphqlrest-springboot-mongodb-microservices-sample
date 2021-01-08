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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.OngoingStubbing;
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
        private ContentService contentService;

        @MockBean
        private ContentMapper contentMapper;

        @Test
        void getAllContents_shouldReturnOk() throws Exception {
                ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);

                Content contentA = new Content();
                contentA.setAvatarId("avatarIdA");
                contentA.setCategoryId("categoryIdA");
                contentA.setImageId("imageIdA");
                contentA.setTitle("Blog A");
                contentA.setBody("Lorem ipsum dolor");
                contentA.setRank(1);
                contentA.setId("A");
                contentA.setCreated(now);
                Content contentB = new Content();
                contentB.setAvatarId("avatarIdB");
                contentB.setCategoryId("categoryIdB");
                contentB.setImageId("imageIdB");
                contentB.setTitle("Blog B");
                contentB.setBody("Ut enim ad minim veniam");
                contentB.setRank(2);
                contentB.setId("B");
                contentB.setCreated(now);
                Collection<Content> contents = ImmutableList.of(contentA, contentB);
                when(contentService.getAllContents()).thenReturn(contents);

                Collection<ContentDTO> contentDTOs = contentMapper.contentsToContentsDTO(contents);
                ContentsDTO contentsDto = ContentsDTO.builder().data(contentDTOs).build();
                this.mockMvc.perform(get("/contents").contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk()).andExpect(content().string(
                                                objectMapper.writeValueAsString(contentsDto)));

                verify(contentService, times(1)).getAllContents();
        }

        @Test
        void getContent_whenExistingId_shouldReturnOk() throws Exception {
                ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
                String existingId = "A";
                Content existingContent = new Content();
                existingContent.setAvatarId("avatarIdA");
                existingContent.setCategoryId("categoryIdA");
                existingContent.setImageId("imageIdA");
                existingContent.setTitle("Blog A");
                existingContent.setBody("Lorem ipsum dolor");
                existingContent.setRank(1);
                existingContent.setId(existingId);
                existingContent.setCreated(now);
                OngoingStubbing<Content> retrievedContent =
                                when(contentService.getContent(existingId))
                                                .thenReturn(existingContent);

                ContentDTO contentDTO =
                                contentMapper.contentToContentDto(retrievedContent.getMock());
                this.mockMvc.perform(get("/contents/{id}", existingId)
                                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                                .andExpect(content().string(
                                                objectMapper.writeValueAsString(contentDTO)));

                verify(contentService, times(1)).getContent(existingId);
        }

        @Test
        void getContent_whenNonexistingId_shouldReturnNotFound() throws Exception {
                String nonExistingId = "Z";
                when(contentService.getContent(nonExistingId))
                                .thenThrow(new ContentNotFoundException(nonExistingId));

                this.mockMvc.perform(get("/contents/{id}", nonExistingId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                                .andExpect(jsonPath("$.message").value(String.format(
                                                "Content with id %s not found.", nonExistingId)));

                verify(contentService, times(1)).getContent(nonExistingId);
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

                verify(contentService, times(0)).getContent(blankId);
        }

        @Test
        void createContent_shouldReturnCreated() throws Exception {
                ContentDTO paramContent = new ContentDTO();
                paramContent.setAvatarId("avatarIdA");
                paramContent.setCategoryId("categoryIdA");
                paramContent.setImageId("imageIdA");
                paramContent.setTitle("Blog A");
                paramContent.setBody("Lorem ipsum dolor");
                paramContent.setRank(1);
                String createdId = "A";
                Content createdContent = contentMapper.contentDtoToContent(paramContent);
                when(contentService.createContent(createdContent)).thenReturn(createdId);

                String createdLocation = "http://localhost/contents/" + createdId;
                this.mockMvc.perform(post("/contents").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(paramContent)))
                                .andExpect(status().isCreated())
                                .andExpect(content().string(createdId))
                                .andExpect(header().string(LOCATION, createdLocation));

                verify(contentService, times(1)).createContent(createdContent);
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
                                                containsString("setAvatarId must not be blank")))
                                .andExpect(jsonPath("$.message").value(
                                                containsString("categoryId must not be blank")))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("title must not be blank")))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("body must not be blank")))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("rank must not be empty")));

                Content content = contentMapper.contentDtoToContent(blankContent);
                verify(contentService, times(0)).createContent(content);
        }

        @Test
        void updateContent_whenExistingId_shouldReturnNoContent() throws Exception {
                String existingId = "A";
                ContentDTO paramContent = new ContentDTO();
                paramContent.setId(existingId);
                paramContent.setAvatarId("setAvatarId");
                paramContent.setCategoryId("categoryId");
                paramContent.setImageId("imageId");
                paramContent.setTitle("Blog A");
                paramContent.setBody("Lorem ipsum dolor");
                paramContent.setRank(1);

                this.mockMvc.perform(put("/contents/{id}", existingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(paramContent)))
                                .andExpect(status().isNoContent());

                Content updatedContent = contentMapper.contentDtoToContent(paramContent);
                verify(contentService, times(1)).updateContent(existingId, updatedContent);
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
                                                containsString("setAvatarId must not be blank")))
                                .andExpect(jsonPath("$.message").value(
                                                containsString("categoryId must not be blank")))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("title must not be blank")))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("body must not be blank")))
                                .andExpect(jsonPath("$.message").value(
                                                containsString("rank must be a positive number")));

                Content content = contentMapper.contentDtoToContent(blankContent);
                verify(contentService, times(0)).updateContent(currentId, content);
        }

        @Test
        void updateContent_whenNonexistingId_shouldReturnNotFound() throws Exception {
                String nonExistingId = "Z";
                ContentDTO paramContent = new ContentDTO();
                paramContent.setId(nonExistingId);
                paramContent.setAvatarId("setAvatarId");
                paramContent.setCategoryId("categoryId");
                paramContent.setImageId("imageId");
                paramContent.setTitle("Blog A");
                paramContent.setBody("Lorem ipsum dolor");
                paramContent.setRank(1);
                Content updatedContent = contentMapper.contentDtoToContent(paramContent);
                doThrow(new ContentNotFoundException(nonExistingId)).when(contentService)
                                .updateContent(nonExistingId, updatedContent);

                this.mockMvc.perform(put("/contents/{id}", nonExistingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(paramContent)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                                .andExpect(jsonPath("$.message").value(String.format(
                                                "Content with id %s not found.", nonExistingId)));

                verify(contentService, times(1)).updateContent(nonExistingId, updatedContent);
        }

        @Test
        void deleteContent_whenExistingId_shouldReturnNoContent() throws Exception {
                final String existingId = "A";

                this.mockMvc.perform(delete("/contents/{id}", existingId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());

                verify(contentService, times(1)).deleteContent(existingId);
        }

        @Test
        void deleteContent_whenBlankId_shouldReturnBadRequest() throws Exception {
                final String blankId = " ";

                this.mockMvc.perform(delete("/contents/{id}", blankId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status")
                                                .value(HttpStatus.BAD_REQUEST.name()))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("must not be blank")));

                verify(contentService, times(0)).deleteContent(blankId);
        }

        @Test
        void deleteContent_whenNonexistingId_shouldReturnNotFound() throws Exception {
                final String nonExistingId = "Z";
                doThrow(new ContentNotFoundException(nonExistingId)).when(contentService)
                                .deleteContent(nonExistingId);

                this.mockMvc.perform(delete("/contents/{id}", nonExistingId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                                .andExpect(jsonPath("$.message").value(String.format(
                                                "Content with id %s not found.", nonExistingId)));

                verify(contentService, times(1)).deleteContent(nonExistingId);
        }
}
