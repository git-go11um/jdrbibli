package com.jdrbibli.userservice.controller;

import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.service.UserProfileService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test unitaire du UserProfileController.
 * Les tests utilisent @WebMvcTest pour charger uniquement le contexte du contrôleur.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserProfileService userProfileService;

    // Si besoin, ajouter pour ignorer les SecurityFilter
    @Autowired
    private org.springframework.context.ApplicationContext context;

    @Test
    void getAllUsers_shouldReturnList() throws Exception {
        UserProfile user1 = new UserProfile();
        user1.setId(1L);
        user1.setPseudo("Alice");

        UserProfile user2 = new UserProfile();
        user2.setId(2L);
        user2.setPseudo("Bob");

        Mockito.when(userProfileService.getAllUsers())
               .thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/api/users").contentType("application/json"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].pseudo").value("Alice"))
               .andExpect(jsonPath("$[1].pseudo").value("Bob"));
    }
}

