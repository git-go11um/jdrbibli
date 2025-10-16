package com.jdrbibli.userservice.controller;

import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.service.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserProfileControllerTest {

    private MockMvc mockMvc;
    private UserProfileService userProfileService;

    @BeforeEach
    void setup() {
        // Création d'un mock du service
        userProfileService = Mockito.mock(UserProfileService.class);

        // Injection dans le contrôleur
        UserProfileController controller = new UserProfileController(userProfileService);

        // Création de MockMvc en mode standalone
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getAllUsers_shouldReturnList() throws Exception {
        // Création de données fictives
        UserProfile user1 = new UserProfile();
        user1.setId(1L);
        user1.setPseudo("Alice");

        UserProfile user2 = new UserProfile();
        user2.setId(2L);
        user2.setPseudo("Bob");

        // Définition du comportement simulé du service
        Mockito.when(userProfileService.getAllUsers())
               .thenReturn(Arrays.asList(user1, user2));

        // Appel du contrôleur et vérifications
        mockMvc.perform(get("/api/users").contentType("application/json"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].pseudo").value("Alice"))
               .andExpect(jsonPath("$[1].pseudo").value("Bob"));
    }
}
