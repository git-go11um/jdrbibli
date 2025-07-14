package com.jdrbibli.userservice.controller;

import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.service.UserProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProfileController.class)
public class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserProfileService userProfileService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserProfile user1;
    private UserProfile user2;

    @BeforeEach
    public void setUp() {
        user1 = new UserProfile();
        user1.setId(1L);
        user1.setPseudo("user1");
        user1.setEmail("user1@example.com");

        user2 = new UserProfile();
        user2.setId(2L);
        user2.setPseudo("user2");
        user2.setEmail("user2@example.com");
    }

    @Test
    public void testGetAllUsers() throws Exception {
        when(userProfileService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].pseudo").value("user1"))
                .andExpect(jsonPath("$[1].pseudo").value("user2"));

        verify(userProfileService, times(1)).getAllUsers();
    }

    @Test
    public void testGetUserById_Found() throws Exception {
        when(userProfileService.getUserById(1L)).thenReturn(Optional.of(user1));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pseudo").value("user1"))
                .andExpect(jsonPath("$.email").value("user1@example.com"));

        verify(userProfileService, times(1)).getUserById(1L);
    }

    @Test
    public void testGetUserById_NotFound() throws Exception {
        when(userProfileService.getUserById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(userProfileService, times(1)).getUserById(999L);
    }

    @Test
    public void testCreateUser() throws Exception {
        UserProfile newUser = new UserProfile();
        newUser.setPseudo("newuser");
        newUser.setEmail("newuser@example.com");

        when(userProfileService.createUser(any(UserProfile.class))).thenReturn(newUser);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pseudo").value("newuser"))
                .andExpect(jsonPath("$.email").value("newuser@example.com"));

        verify(userProfileService, times(1)).createUser(any(UserProfile.class));
    }

    @Test
    public void testDeleteUser() throws Exception {
        doNothing().when(userProfileService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userProfileService, times(1)).deleteUser(1L);
    }
}
