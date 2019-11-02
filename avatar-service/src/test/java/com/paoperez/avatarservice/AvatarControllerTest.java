package com.paoperez.avatarservice;

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
class AvatarControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AvatarService service;

    @Test
    void getAllAvatars_shouldReturnOk() throws Exception {
        final Avatar avatarA = Avatar.builder().id("A").userName("userA").imageId("imageIdA").build();
        final Avatar avatarB = Avatar.builder().id("B").userName("userB").build();
        final Collection<Avatar> avatars = ImmutableList.of(avatarA, avatarB);
        when(service.getAllAvatars()).thenReturn(avatars);

        this.mockMvc.perform(get("/avatars").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(avatars)));

        verify(service, times(1)).getAllAvatars();
    }

    @Test
    void getAvatar_whenExistingId_shouldReturnOk() throws Exception {
        final String existingId = "A";
        final Avatar existingAvatar = Avatar.builder().id(existingId).userName("userA").imageId("imageIdA").build();
        when(service.getAvatar(existingId)).thenReturn(existingAvatar);

        this.mockMvc.perform(get("/avatars/{id}", existingId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(existingAvatar)));

        verify(service, times(1)).getAvatar(existingId);
    }

    @Test
    void getAvatar_whenNonexistingId_shouldReturnNotFound() throws Exception {
        final String nonExistingId = "Z";
        when(service.getAvatar(nonExistingId)).thenThrow(new AvatarNotFoundException(nonExistingId));

        this.mockMvc.perform(get("/avatars/{id}", nonExistingId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(String.format("Avatar with id %s not found.", nonExistingId)));

        verify(service, times(1)).getAvatar(nonExistingId);
    }

    @Test
    void getAvatar_whenBlankId_shouldReturnBadRequest() throws Exception {
        final String blankId = " ";

        this.mockMvc.perform(get("/avatars/{id}", blankId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value(containsString("must not be blank")));

        verify(service, times(0)).getAvatar(blankId);
    }

    @Test
    void createAvatar_whenNonexistingUserName_shouldReturnCreated() throws Exception {
        final String nonExistingUserName = "userA";
        final Avatar newAvatar = Avatar.builder().userName(nonExistingUserName).build();
        final String createdId = "A";
        final Avatar createdAvatar = Avatar.builder().id(createdId).userName(nonExistingUserName).build();
        final String createdLocation = "http://localhost/avatars/" + createdId;
        when(service.createAvatar(newAvatar)).thenReturn(createdAvatar);

        this.mockMvc
                .perform(post("/avatars").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAvatar)))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(createdAvatar)))
                .andExpect(header().string(LOCATION, createdLocation));

        verify(service, times(1)).createAvatar(newAvatar);
    }

    @Test
    void createAvatar_whenExistingUserName_shouldReturnConflict() throws Exception {
        final String existingUserName = "userA";
        final Avatar newAvatar = Avatar.builder().userName(existingUserName).build();
        when(service.createAvatar(newAvatar)).thenThrow(new AvatarAlreadyExistsException(existingUserName));

        this.mockMvc
                .perform(post("/avatars").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAvatar)))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.name()))
                .andExpect(jsonPath("$.message")
                        .value(String.format("Avatar with userName %s already exists.", existingUserName)));

        verify(service, times(1)).createAvatar(newAvatar);
    }

    @Test
    void createAvatar_whenBlankUserName_shouldReturnBadRequest() throws Exception {
        final Avatar blankAvatar = Avatar.builder().userName(" ").build();

        this.mockMvc
                .perform(post("/avatars").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blankAvatar)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value(containsString("userName must not be blank")));

        verify(service, times(0)).createAvatar(blankAvatar);
    }

    @Test
    void updateAvatar_whenExistingId_shouldReturnNoContent() throws Exception {
        final String existingId = "A";
        final Avatar updateAvatar = Avatar.builder().id(existingId).userName("userA").imageId("imageIdA").build();

        this.mockMvc.perform(put("/avatars/{id}", existingId).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateAvatar))).andExpect(status().isNoContent());

        verify(service, times(1)).updateAvatar(existingId, updateAvatar);
    }

    @Test
    void updateAvatar_whenExistingUserName_shouldReturnConflict() throws Exception {
        final String currentId = "A";
        final String existingUserName = "userA";
        final Avatar currentAvatar = Avatar.builder().id(currentId).userName(existingUserName).imageId("imageIdA")
                .build();
        doThrow(new AvatarAlreadyExistsException(existingUserName)).when(service).updateAvatar(currentId,
                currentAvatar);

        this.mockMvc
                .perform(put("/avatars/{id}", currentId).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(currentAvatar)))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.name()))
                .andExpect(jsonPath("$.message")
                        .value(String.format("Avatar with userName %s already exists.", existingUserName)));

        verify(service, times(1)).updateAvatar(currentId, currentAvatar);
    }

    @Test
    void updateAvatar_whenBlankUserName_shouldReturnBadRequest() throws Exception {
        final String currentId = "A";
        final Avatar blankAvatar = Avatar.builder().id(currentId).userName(" ").imageId("imageIdA").build();

        this.mockMvc
                .perform(put("/avatars/{id}", currentId).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blankAvatar)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value(containsString("userName must not be blank")));

        verify(service, times(0)).updateAvatar(currentId, blankAvatar);
    }

    @Test
    void updateAvatar_whenNonexistingId_shouldReturnNotFound() throws Exception {
        final String nonExistingId = "Z";
        final Avatar nonExistingAvatar = Avatar.builder().id(nonExistingId).userName("userA").imageId("imageIdA")
                .build();
        doThrow(new AvatarNotFoundException(nonExistingId)).when(service).updateAvatar(nonExistingId,
                nonExistingAvatar);

        this.mockMvc
                .perform(put("/avatars/{id}", nonExistingId).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistingAvatar)))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(String.format("Avatar with id %s not found.", nonExistingId)));

        verify(service, times(1)).updateAvatar(nonExistingId, nonExistingAvatar);
    }

    @Test
    void deleteAvatar_whenExistingId_shouldReturnNoContent() throws Exception {
        final String existingId = "A";

        this.mockMvc.perform(delete("/avatars/{id}", existingId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteAvatar(existingId);
    }

    @Test
    void deleteAvatar_whenBlankId_shouldReturnBadRequest() throws Exception {
        final String blankId = " ";

        this.mockMvc.perform(delete("/avatars/{id}", blankId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value(containsString("must not be blank")));

        verify(service, times(0)).deleteAvatar(blankId);
    }

    @Test
    void deleteAvatar_whenNonexistingId_shouldReturnNotFound() throws Exception {
        final String nonExistingId = "Z";
        doThrow(new AvatarNotFoundException(nonExistingId)).when(service).deleteAvatar(nonExistingId);

        this.mockMvc.perform(delete("/avatars/{id}", nonExistingId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(String.format("Avatar with id %s not found.", nonExistingId)));

        verify(service, times(1)).deleteAvatar(nonExistingId);
    }

}