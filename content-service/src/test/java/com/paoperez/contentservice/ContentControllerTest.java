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
import java.time.LocalDateTime;
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
class ContentControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private ContentService service;

        @Test
        void getAllContents_shouldReturnOk() throws Exception {
                final Content contentA = new Content.Builder().avatarId("avatarIdA")
                                .categoryId("categoryIdA").imageId("imageIdA").title("Blog A")
                                .body("Lorem ipsum dolor").rank(1).id("A")
                                .created(LocalDateTime.now().toString()).build();
                final Content contentB = new Content.Builder().avatarId("avatarIdB")
                                .categoryId("categoryIdB").imageId("imageIdB").title("Blog B")
                                .body("Ut enim ad minim veniam").rank(2).id("B")
                                .created(LocalDateTime.now().toString()).build();

                final Collection<Content> data = ImmutableList.of(contentA, contentB);
                final Contents contents = Contents.builder().data(data).build();
                when(service.getAllContents()).thenReturn(data);

                this.mockMvc.perform(get("/contents").contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk()).andExpect(content()
                                                .string(objectMapper.writeValueAsString(contents)));

                verify(service, times(1)).getAllContents();
        }

        @Test
        void getContent_whenExistingId_shouldReturnOk() throws Exception {
                final String existingId = "A";
                final Content existingContent = new Content.Builder().avatarId("avatarIdA")
                                .categoryId("categoryIdA").imageId("imageIdA").title("Blog A")
                                .body("Lorem ipsum dolor").rank(1).id(existingId)
                                .created(LocalDateTime.now().toString()).build();
                when(service.getContent(existingId)).thenReturn(existingContent);

                this.mockMvc.perform(get("/contents/{id}", existingId)
                                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                                .andExpect(content().string(
                                                objectMapper.writeValueAsString(existingContent)));

                verify(service, times(1)).getContent(existingId);
        }

        @Test
        void getContent_whenNonexistingId_shouldReturnNotFound() throws Exception {
                final String nonExistingId = "Z";
                when(service.getContent(nonExistingId))
                                .thenThrow(new ContentNotFoundException(nonExistingId));

                this.mockMvc.perform(get("/contents/{id}", nonExistingId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                                .andExpect(jsonPath("$.message").value(String.format(
                                                "Content with id %s not found.", nonExistingId)));

                verify(service, times(1)).getContent(nonExistingId);
        }

        @Test
        void getContent_whenBlankId_shouldReturnBadRequest() throws Exception {
                final String blankId = " ";

                this.mockMvc.perform(get("/contents/{id}", blankId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status")
                                                .value(HttpStatus.BAD_REQUEST.name()))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("must not be blank")));

                verify(service, times(0)).getContent(blankId);
        }

        @Test
        void createContent_shouldReturnCreated() throws Exception {
                final Content paramContent = new Content.Builder().avatarId("avatarIdA")
                                .categoryId("categoryIdA").imageId("imageIdA").title("Blog A")
                                .body("Lorem ipsum dolor").rank(1).build();
                final String createdId = "A";
                final Content createdContent = new Content.Builder().from(paramContent)
                                .created(LocalDateTime.now().toString()).id(createdId).build();
                when(service.createContent(paramContent)).thenReturn(createdContent);

                final String createdLocation = "http://localhost/contents/" + createdId;
                this.mockMvc.perform(post("/contents").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(paramContent)))
                                .andExpect(status().isCreated())
                                .andExpect(content().string(
                                                objectMapper.writeValueAsString(createdContent)))
                                .andExpect(header().string(LOCATION, createdLocation));

                verify(service, times(1)).createContent(paramContent);
        }

        @Test
        void createContent_whenBlankFields_shouldReturnBadRequest() throws Exception {
                final Content blankContent = new Content.Builder().build();

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

                verify(service, times(0)).createContent(blankContent);
        }

        @Test
        void createContent_whenServiceThrowsIllegalStateException_shouldReturnInternalServerError()
                        throws Exception {
                final Content paramContent = new Content.Builder().avatarId("avatarIdA")
                                .categoryId("categoryIdA").imageId("imageIdA").title("Blog A")
                                .body("Lorem ipsum dolor").rank(1).build();
                doThrow(new IllegalStateException(
                                "Failed to create DateTime instance, DateTimeFactory in Builder is null."))
                                                .when(service).createContent(paramContent);

                this.mockMvc.perform(post("/contents").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(paramContent)))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status")
                                                .value(HttpStatus.INTERNAL_SERVER_ERROR.name()))
                                .andExpect(jsonPath("$.message").value(
                                                "There is an internal server error. We will look into it and update the site soon."));

                verify(service, times(1)).createContent(paramContent);
        }

        @Test
        void updateContent_whenExistingId_shouldReturnNoContent() throws Exception {
                final String existingId = "A";
                final Content paramContent = new Content.Builder().id(existingId)
                                .avatarId("avatarId").categoryId("categoryId").imageId("imageId")
                                .title("Blog A").body("Lorem ipsum dolor").rank(1)
                                .created(LocalDateTime.now().toString()).build();

                this.mockMvc.perform(put("/contents/{id}", existingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(paramContent)))
                                .andExpect(status().isNoContent());

                verify(service, times(1)).updateContent(existingId, paramContent);
        }

        @Test
        void updateContent_whenBlankFields_shouldReturnBadRequest() throws Exception {
                final String currentId = "A";
                final Content blankContent = new Content.Builder().id(currentId).rank(0).build();

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

                verify(service, times(0)).updateContent(currentId, blankContent);
        }

        @Test
        void updateContent_whenNonexistingId_shouldReturnNotFound() throws Exception {
                final String nonExistingId = "Z";
                final Content paramContent = new Content.Builder().id(nonExistingId)
                                .avatarId("avatarId").categoryId("categoryId").imageId("imageId")
                                .title("Blog A").body("Lorem ipsum dolor").rank(1)
                                .created(LocalDateTime.now().toString()).build();
                doThrow(new ContentNotFoundException(nonExistingId)).when(service)
                                .updateContent(nonExistingId, paramContent);

                this.mockMvc.perform(put("/contents/{id}", nonExistingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(paramContent)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                                .andExpect(jsonPath("$.message").value(String.format(
                                                "Content with id %s not found.", nonExistingId)));

                verify(service, times(1)).updateContent(nonExistingId, paramContent);
        }

        @Test
        void updateContent_whenServiceThrowsIllegalStateException_shouldReturnInternalServerError()
                        throws Exception {
                final String existingId = "Z";
                final Content paramContent = new Content.Builder().id(existingId)
                                .avatarId("avatarId").categoryId("categoryId").imageId("imageId")
                                .title("Blog A").body("Lorem ipsum dolor").rank(1)
                                .created(LocalDateTime.now().toString()).build();
                doThrow(new IllegalStateException(
                                "Failed to update DateTime instance, DateTimeFactory in Builder is null."))
                                                .when(service)
                                                .updateContent(existingId, paramContent);

                this.mockMvc.perform(put("/contents/{id}", existingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(paramContent)))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status")
                                                .value(HttpStatus.INTERNAL_SERVER_ERROR.name()))
                                .andExpect(jsonPath("$.message").value(
                                                "There is an internal server error. We will look into it and update the site soon."));

                verify(service, times(1)).updateContent(existingId, paramContent);
        }

        @Test
        void deleteContent_whenExistingId_shouldReturnNoContent() throws Exception {
                final String existingId = "A";

                this.mockMvc.perform(delete("/contents/{id}", existingId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());

                verify(service, times(1)).deleteContent(existingId);
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

                verify(service, times(0)).deleteContent(blankId);
        }

        @Test
        void deleteContent_whenNonexistingId_shouldReturnNotFound() throws Exception {
                final String nonExistingId = "Z";
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
