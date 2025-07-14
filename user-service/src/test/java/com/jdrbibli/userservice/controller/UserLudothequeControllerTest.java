package com.jdrbibli.userservice.controller;

import com.jdrbibli.userservice.dto.OuvrageDTO;
import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.repository.UserProfileRepository;
import com.jdrbibli.userservice.service.UserLudothequeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserLudothequeController.class)
public class UserLudothequeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserProfileRepository userProfileRepository;

    @MockBean
    private UserLudothequeService userLudothequeService;

    private UserProfile user;

    @BeforeEach
    public void setUp() {
        user = new UserProfile();
        user.setId(1L);
        user.setPseudo("testUser");
        user.setOuvrageIds(new ArrayList<>());
    }

    @Test
    public void testAddOuvrageToLudotheque_Success() throws Exception {
        OuvrageDTO ouvrageDTO = new OuvrageDTO();
        ouvrageDTO.setId(100L);
        ouvrageDTO.setTitre("Test Ouvrage");

        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userLudothequeService.getOuvrageById(100L)).thenReturn(ouvrageDTO);
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(user);

        mockMvc.perform(post("/api/users/1/ludotheque/100")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Ouvrage ajouté à la ludothèque !"));

        // Vérifie que l'ID de l'ouvrage a bien été ajouté
        assert (user.getOuvrageIds().contains(100L));
        verify(userProfileRepository).save(user);
    }

    @Test
    public void testAddOuvrageToLudotheque_OuvrageNotFound() throws Exception {
        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userLudothequeService.getOuvrageById(100L)).thenReturn(null);

        mockMvc.perform(post("/api/users/1/ludotheque/100")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userProfileRepository, never()).save(any());
    }

    @Test
    public void testAddOuvrageToLudotheque_UserNotFound() throws Exception {
        when(userProfileRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/users/1/ludotheque/100")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userProfileRepository, never()).save(any());
    }

    @Test
    public void testRemoveOuvrageFromLudotheque_Success() throws Exception {
        user.getOuvrageIds().add(100L);
        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(user);

        mockMvc.perform(delete("/api/users/1/ludotheque/100")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Ouvrage retiré de la ludothèque !"));

        // Vérifie que l'ID a bien été retiré
        assert (!user.getOuvrageIds().contains(100L));
        verify(userProfileRepository).save(user);
    }

    @Test
    public void testRemoveOuvrageFromLudotheque_UserNotFound() throws Exception {
        when(userProfileRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/users/1/ludotheque/100")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userProfileRepository, never()).save(any());
    }
}
