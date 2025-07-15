package com.jdrbibli.authservice.security;

import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository; // Création du mock pour UserRepository

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService; // Injection du mock dans le service

    @BeforeEach
    void setUp() {
        // Initialisation explicite des mocks
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername_whenUserExists() {
        // Arrange
        String pseudo = "testUser";
        User mockUser = new User();
        mockUser.setPseudo(pseudo); // Création de l'utilisateur fictif
        when(userRepository.findByPseudo(pseudo)).thenReturn(java.util.Optional.of(mockUser)); // Simuler le
                                                                                               // comportement du
                                                                                               // repository

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(pseudo); // Charger l'utilisateur

        // Assert
        assertNotNull(userDetails); // Vérifier que l'utilisateur est trouvé
        assertEquals(pseudo, userDetails.getUsername()); // Vérifier que le pseudo est correct
        verify(userRepository, times(1)).findByPseudo(pseudo); // Vérifier que findByPseudo a bien été appelé une fois
    }

    @Test
    void testLoadUserByUsername_whenUserDoesNotExist() {
        // Arrange
        String pseudo = "nonExistentUser";
        when(userRepository.findByPseudo(pseudo)).thenReturn(java.util.Optional.empty()); // Simuler l'absence
                                                                                          // d'utilisateur

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername(pseudo)); // Vérifier
                                                                                                                  // l'exception
                                                                                                                  // levée
        verify(userRepository, times(1)).findByPseudo(pseudo); // Vérifier que findByPseudo a bien été appelé une fois
    }
}
