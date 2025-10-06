package com.jdrbibli.userservice.controller;

import com.jdrbibli.userservice.dto.OuvrageDTO;
import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.repository.UserProfileRepository;
import com.jdrbibli.userservice.service.UserLudothequeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserLudothequeControllerTest {

    private UserProfileRepository userProfileRepository;
    private UserLudothequeService userLudothequeService;
    private UserLudothequeController controller;

    @BeforeEach
    void setUp() {
        userProfileRepository = mock(UserProfileRepository.class);
        userLudothequeService = mock(UserLudothequeService.class);
        controller = new UserLudothequeController(userProfileRepository, userLudothequeService);
    }

    @Test
    void addOuvrageToLudotheque_shouldAddOuvrage() {
        Long userId = 1L;
        Long ouvrageId = 100L;

        UserProfile user = new UserProfile();
        user.setId(userId);
        user.setOuvrageIds(new ArrayList<>());

        OuvrageDTO ouvrageDTO = new OuvrageDTO();
        ouvrageDTO.setId(ouvrageId);

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userLudothequeService.getOuvrageById(ouvrageId)).thenReturn(ouvrageDTO);
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(user);

        ResponseEntity<?> response = controller.addOuvrageToLudotheque(userId, ouvrageId);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(user.getOuvrageIds()).contains(ouvrageId);

        // Vérifie que save a bien été appelé
        ArgumentCaptor<UserProfile> captor = ArgumentCaptor.forClass(UserProfile.class);
        verify(userProfileRepository).save(captor.capture());
        assertThat(captor.getValue().getOuvrageIds()).contains(ouvrageId);
    }

    @Test
    void addOuvrageToLudotheque_shouldReturnNotFoundIfUserMissing() {
        Long userId = 1L;
        Long ouvrageId = 100L;

        when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.addOuvrageToLudotheque(userId, ouvrageId);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    void addOuvrageToLudotheque_shouldReturnNotFoundIfOuvrageMissing() {
        Long userId = 1L;
        Long ouvrageId = 100L;

        UserProfile user = new UserProfile();
        user.setId(userId);
        user.setOuvrageIds(new ArrayList<>());

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userLudothequeService.getOuvrageById(ouvrageId)).thenReturn(null);

        ResponseEntity<?> response = controller.addOuvrageToLudotheque(userId, ouvrageId);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    void removeOuvrageFromLudotheque_shouldRemoveOuvrage() {
        Long userId = 1L;
        Long ouvrageId = 100L;

        UserProfile user = new UserProfile();
        user.setId(userId);
        List<Long> ouvrageIds = new ArrayList<>();
        ouvrageIds.add(ouvrageId);
        user.setOuvrageIds(ouvrageIds);

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(user);

        ResponseEntity<?> response = controller.removeOuvrageFromLudotheque(userId, ouvrageId);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(user.getOuvrageIds()).doesNotContain(ouvrageId);

        // Vérifie que save a bien été appelé
        ArgumentCaptor<UserProfile> captor = ArgumentCaptor.forClass(UserProfile.class);
        verify(userProfileRepository).save(captor.capture());
        assertThat(captor.getValue().getOuvrageIds()).doesNotContain(ouvrageId);
    }

    @Test
    void removeOuvrageFromLudotheque_shouldReturnNotFoundIfUserMissing() {
        Long userId = 1L;
        Long ouvrageId = 100L;

        when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.removeOuvrageFromLudotheque(userId, ouvrageId);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }
}
