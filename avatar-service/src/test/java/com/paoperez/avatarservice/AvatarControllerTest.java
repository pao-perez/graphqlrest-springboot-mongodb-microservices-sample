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

        @MockBean
        private AvatarMapper avatarMapper;

        @Test
        void getAllAvatars_shouldReturnOk() throws Exception {
                Avatar avatarA = new Avatar();
                avatarA.setUserName("userA");
                avatarA.setImageId("imageIdA");
                avatarA.setId("A");
                Avatar avatarB = new Avatar();
                avatarB.setUserName("userB");
                avatarB.setImageId("imageIdB");
                avatarB.setId("B");
                Collection<Avatar> avatars = ImmutableList.of(avatarA, avatarB);
                when(service.getAllAvatars()).thenReturn(avatars);

                AvatarDTO avatarDtoA = new AvatarDTO();
                avatarDtoA.setUserName("userA");
                avatarDtoA.setImageId("imageIdA");
                avatarDtoA.setId("A");
                AvatarDTO avatarDtoB = new AvatarDTO();
                avatarDtoB.setUserName("userB");
                avatarDtoB.setImageId("imageIdB");
                avatarDtoB.setId("B");
                Collection<AvatarDTO> avatarDTOs = ImmutableList.of(avatarDtoA, avatarDtoB);
                when(avatarMapper.avatarsToAvatarsDTO(avatars)).thenReturn(avatarDTOs);

                AvatarsDTO avatarsDto = AvatarsDTO.builder().data(avatarDTOs).build();
                this.mockMvc.perform(get("/avatars").contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk()).andExpect(content().string(
                                                objectMapper.writeValueAsString(avatarsDto)));

                verify(service, times(1)).getAllAvatars();
                verify(avatarMapper, times(1)).avatarsToAvatarsDTO(avatars);
        }

        @Test
        void getAvatar_whenExistingId_shouldReturnOk() throws Exception {
                String existingId = "A";

                Avatar existingAvatar = new Avatar();
                existingAvatar.setId(existingId);
                existingAvatar.setUserName("userA");
                existingAvatar.setImageId("imageIdA");
                when(service.getAvatar(existingId)).thenReturn(existingAvatar);

                AvatarDTO existingAvatarDto = new AvatarDTO();
                existingAvatarDto.setId(existingId);
                existingAvatarDto.setUserName("userA");
                existingAvatarDto.setImageId("imageIdA");
                when(avatarMapper.avatarToAvatarDto(existingAvatar)).thenReturn(existingAvatarDto);

                this.mockMvc.perform(get("/avatars/{id}", existingId)
                                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                                .andExpect(content().string(objectMapper
                                                .writeValueAsString(existingAvatarDto)));

                verify(service, times(1)).getAvatar(existingId);
                verify(avatarMapper, times(1)).avatarToAvatarDto(existingAvatar);
        }

        @Test
        void getAvatar_whenNonexistingId_shouldReturnNotFound() throws Exception {
                String nonExistingId = "Z";
                when(service.getAvatar(nonExistingId))
                                .thenThrow(new AvatarNotFoundException(nonExistingId));

                this.mockMvc.perform(get("/avatars/{id}", nonExistingId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                                .andExpect(jsonPath("$.message").value(String.format(
                                                "Avatar with id %s not found.", nonExistingId)));

                verify(service, times(1)).getAvatar(nonExistingId);
                verify(avatarMapper, times(0)).avatarToAvatarDto(null);
        }

        @Test
        void getAvatar_whenBlankId_shouldReturnBadRequest() throws Exception {
                String blankId = " ";

                this.mockMvc.perform(get("/avatars/{id}", blankId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status")
                                                .value(HttpStatus.BAD_REQUEST.name()))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("must not be blank")));

                verify(service, times(0)).getAvatar(blankId);
                verify(avatarMapper, times(0)).avatarToAvatarDto(null);
        }

        @Test
        void createAvatar_whenNonexistingUserName_shouldReturnCreated() throws Exception {
                String nonExistingUserName = "A";

                AvatarDTO avatarDto = new AvatarDTO();
                avatarDto.setUserName(nonExistingUserName);
                avatarDto.setImageId("imageIdA");
                Avatar avatar = new Avatar();
                avatar.setUserName(nonExistingUserName);
                avatar.setImageId("imageIdA");
                when(avatarMapper.avatarDtoToAvatar(avatarDto)).thenReturn(avatar);

                String createdId = "A";
                String createdLocation = "http://localhost/avatars/" + createdId;
                when(service.createAvatar(avatar)).thenReturn(createdId);

                this.mockMvc.perform(post("/avatars").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(avatarDto)))
                                .andExpect(status().isCreated())
                                .andExpect(content().string(createdId))
                                .andExpect(header().string(LOCATION, createdLocation));

                verify(avatarMapper, times(1)).avatarDtoToAvatar(avatarDto);
                verify(service, times(1)).createAvatar(avatar);
        }

        @Test
        void createAvatar_whenExistingUserName_shouldReturnConflict() throws Exception {
                String existingUserName = "userA";

                AvatarDTO avatarDto = new AvatarDTO();
                avatarDto.setUserName(existingUserName);
                avatarDto.setImageId("imageIdA");
                Avatar avatar = new Avatar();
                avatar.setUserName(existingUserName);
                avatar.setImageId("imageIdA");
                when(avatarMapper.avatarDtoToAvatar(avatarDto)).thenReturn(avatar);

                when(service.createAvatar(avatar))
                                .thenThrow(new AvatarAlreadyExistsException(existingUserName));

                this.mockMvc.perform(post("/avatars").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(avatarDto)))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.name()))
                                .andExpect(jsonPath("$.message").value(String.format(
                                                "Avatar with userName %s already exists.",
                                                existingUserName)));

                verify(avatarMapper, times(1)).avatarDtoToAvatar(avatarDto);
                verify(service, times(1)).createAvatar(avatar);
        }

        @Test
        void createAvatar_whenBlankFields_shouldReturnBadRequest() throws Exception {
                AvatarDTO blankAvatar = new AvatarDTO();

                this.mockMvc.perform(post("/avatars").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(blankAvatar)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status")
                                                .value(HttpStatus.BAD_REQUEST.name()))
                                .andExpect(jsonPath("$.message").value(
                                                containsString("userName must not be blank")))
                                .andExpect(jsonPath("$.message").value(
                                                containsString("imageId must not be blank")));

                verify(avatarMapper, times(0)).avatarDtoToAvatar(null);
                verify(service, times(0)).createAvatar(null);
        }

        @Test
        void updateAvatar_whenExistingId_shouldReturnNoContent() throws Exception {
                String existingId = "A";

                AvatarDTO avatarDto = new AvatarDTO();
                avatarDto.setUserName("userA");
                avatarDto.setImageId("imageIdA");
                avatarDto.setId(existingId);
                Avatar avatar = new Avatar();
                avatar.setUserName("userA");
                avatar.setImageId("imageIdA");
                avatar.setId(existingId);
                when(avatarMapper.avatarDtoToAvatar(avatarDto)).thenReturn(avatar);

                this.mockMvc.perform(put("/avatars/{id}", existingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(avatarDto)))
                                .andExpect(status().isNoContent());

                verify(avatarMapper, times(1)).avatarDtoToAvatar(avatarDto);
                verify(service, times(1)).updateAvatar(existingId, avatar);
        }

        @Test
        void updateAvatar_whenExistingUserName_shouldReturnConflict() throws Exception {
                String id = "A";
                String existingUserName = "userA";

                AvatarDTO avatarDto = new AvatarDTO();
                avatarDto.setUserName(existingUserName);
                avatarDto.setImageId("imageIdA");
                avatarDto.setId(id);
                Avatar avatar = new Avatar();
                avatar.setUserName(existingUserName);
                avatar.setImageId("imageIdA");
                avatar.setId(id);
                when(avatarMapper.avatarDtoToAvatar(avatarDto)).thenReturn(avatar);

                doThrow(new AvatarAlreadyExistsException(existingUserName)).when(service)
                                .updateAvatar(id, avatar);

                this.mockMvc.perform(
                                put("/avatars/{id}", id).contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper
                                                                .writeValueAsString(avatarDto)))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.name()))
                                .andExpect(jsonPath("$.message").value(String.format(
                                                "Avatar with userName %s already exists.",
                                                existingUserName)));

                verify(avatarMapper, times(1)).avatarDtoToAvatar(avatarDto);
                verify(service, times(1)).updateAvatar(id, avatar);
        }

        @Test
        void updateAvatar_whenBlankFields_shouldReturnBadRequest() throws Exception {
                String id = "A";
                AvatarDTO blankAvatar = new AvatarDTO();
                blankAvatar.setId(id);

                this.mockMvc.perform(
                                put("/avatars/{id}", id).contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper
                                                                .writeValueAsString(blankAvatar)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status")
                                                .value(HttpStatus.BAD_REQUEST.name()))
                                .andExpect(jsonPath("$.message").value(
                                                containsString("userName must not be blank")))
                                .andExpect(jsonPath("$.message").value(
                                                containsString("imageId must not be blank")));

                verify(avatarMapper, times(0)).avatarDtoToAvatar(null);
                verify(service, times(0)).updateAvatar(null, null);
        }

        @Test
        void updateAvatar_whenMismatchId_shouldReturnBadRequest() throws Exception {
                String differentId = "differentId";

                AvatarDTO avatarDto = new AvatarDTO();
                avatarDto.setUserName("userA");
                avatarDto.setImageId("imageIdA");
                avatarDto.setId(differentId);
                Avatar avatar = new Avatar();
                avatar.setUserName("userA");
                avatar.setImageId("imageIdA");
                avatar.setId(differentId);
                when(avatarMapper.avatarDtoToAvatar(avatarDto)).thenReturn(avatar);

                String id = "A";
                doThrow(new AvatarMismatchException(id, differentId)).when(service).updateAvatar(id,
                                avatar);

                this.mockMvc.perform(
                                put("/avatars/{id}", id).contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper
                                                                .writeValueAsString(avatarDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status")
                                                .value(HttpStatus.BAD_REQUEST.name()))
                                .andExpect(jsonPath("$.message").value(String.format(
                                                "Avatar with id %s does not match avatar argument %s.",
                                                id, differentId)));

                verify(avatarMapper, times(1)).avatarDtoToAvatar(avatarDto);
                verify(service, times(1)).updateAvatar(id, avatar);
        }

        @Test
        void updateAvatar_whenNonexistingId_shouldReturnNotFound() throws Exception {
                String nonExistingId = "Z";

                AvatarDTO avatarDto = new AvatarDTO();
                avatarDto.setUserName("userA");
                avatarDto.setImageId("imageIdA");
                avatarDto.setId(nonExistingId);
                Avatar avatar = new Avatar();
                avatar.setUserName("userA");
                avatar.setImageId("imageIdA");
                avatar.setId(nonExistingId);
                when(avatarMapper.avatarDtoToAvatar(avatarDto)).thenReturn(avatar);

                doThrow(new AvatarNotFoundException(nonExistingId)).when(service)
                                .updateAvatar(nonExistingId, avatar);

                this.mockMvc.perform(put("/avatars/{id}", nonExistingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(avatarDto)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                                .andExpect(jsonPath("$.message").value(String.format(
                                                "Avatar with id %s not found.", nonExistingId)));

                verify(avatarMapper, times(1)).avatarDtoToAvatar(avatarDto);
                verify(service, times(1)).updateAvatar(nonExistingId, avatar);
        }

        @Test
        void deleteAvatar_whenExistingId_shouldReturnNoContent() throws Exception {
                String existingId = "A";

                this.mockMvc.perform(delete("/avatars/{id}", existingId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());

                verify(service, times(1)).deleteAvatar(existingId);
        }

        @Test
        void deleteAvatar_whenBlankId_shouldReturnBadRequest() throws Exception {
                String blankId = " ";

                this.mockMvc.perform(delete("/avatars/{id}", blankId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status")
                                                .value(HttpStatus.BAD_REQUEST.name()))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("must not be blank")));

                verify(service, times(0)).deleteAvatar(blankId);
        }

        @Test
        void deleteAvatar_whenNonexistingId_shouldReturnNotFound() throws Exception {
                String nonExistingId = "Z";
                doThrow(new AvatarNotFoundException(nonExistingId)).when(service)
                                .deleteAvatar(nonExistingId);

                this.mockMvc.perform(delete("/avatars/{id}", nonExistingId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                                .andExpect(jsonPath("$.message").value(String.format(
                                                "Avatar with id %s not found.", nonExistingId)));

                verify(service, times(1)).deleteAvatar(nonExistingId);
        }
}
