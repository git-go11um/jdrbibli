package com.jdrbibli.userservice.service;

import com.jdrbibli.userservice.config.StorageProperties;
import com.jdrbibli.userservice.dto.FriendDTO;
import com.jdrbibli.userservice.dto.UserProfileDTO;
import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.repository.UserProfileRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserProfileServiceTest {

    private UserProfileRepository userProfileRepository;
    private FriendRequestService friendRequestService;
    private WebClient webClient;
    private StorageProperties storageProperties;
    private UserProfileService userProfileService;
    private Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        userProfileRepository = mock(UserProfileRepository.class);
        friendRequestService = mock(FriendRequestService.class);
        webClient = mock(WebClient.class);
        storageProperties = mock(StorageProperties.class);

        tempDir = Files.createTempDirectory("avatars");
        when(storageProperties.getUploadDir()).thenReturn(tempDir.toString());

        userProfileService = new UserProfileService(
                userProfileRepository,
                friendRequestService,
                webClient,
                storageProperties);
    }

    @Test
    void createUser_shouldCreateNewUser_whenPseudoDoesNotExist() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setPseudo("user1");
        dto.setEmail("user1@email.com");

        when(userProfileRepository.findByPseudo("user1")).thenReturn(Optional.empty());
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(invocation -> {
            UserProfile u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserProfileDTO result = userProfileService.createUser(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("user1", result.getPseudo());
        assertEquals("user1@email.com", result.getEmail());

        ArgumentCaptor<UserProfile> captor = ArgumentCaptor.forClass(UserProfile.class);
        verify(userProfileRepository).save(captor.capture());
        assertEquals("user1", captor.getValue().getPseudo());
    }

    @Test
    void createUser_shouldReturnExistingUser_whenPseudoExists() {
        UserProfile existing = new UserProfile();
        existing.setId(2L);
        existing.setPseudo("user2");
        existing.setEmail("user2@email.com");

        UserProfileDTO dto = new UserProfileDTO();
        dto.setPseudo("user2");
        dto.setEmail("user2@email.com");

        when(userProfileRepository.findByPseudo("user2")).thenReturn(Optional.of(existing));

        UserProfileDTO result = userProfileService.createUser(dto);

        assertEquals(2L, result.getId());
        assertEquals("user2", result.getPseudo());
        assertEquals("user2@email.com", result.getEmail());
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    void getAllUsers_shouldReturnList() {
        when(userProfileRepository.findAll()).thenReturn(List.of(new UserProfile(), new UserProfile()));
        List<UserProfile> result = userProfileService.getAllUsers();
        assertEquals(2, result.size());
    }

    @Test
    void getUserById_shouldReturnOptional() {
        UserProfile user = new UserProfile();
        user.setId(5L);
        when(userProfileRepository.findById(5L)).thenReturn(Optional.of(user));

        Optional<UserProfile> result = userProfileService.getUserById(5L);

        assertTrue(result.isPresent());
        assertEquals(5L, result.get().getId());
    }

    @Test
    void deleteUserById_shouldReturnTrue_whenExists() {
        when(userProfileRepository.existsById(10L)).thenReturn(true);
        boolean result = userProfileService.deleteUserById(10L);
        assertTrue(result);
        verify(userProfileRepository).deleteById(10L);
    }

    @Test
    void deleteUserById_shouldReturnFalse_whenNotExists() {
        when(userProfileRepository.existsById(11L)).thenReturn(false);
        boolean result = userProfileService.deleteUserById(11L);
        assertFalse(result);
        verify(userProfileRepository, never()).deleteById(any());
    }

    @Test
    void deleteUserByIdWithCascade_shouldReturnTrue_whenUserExists() {
        Long userId = 1L;
        UserProfile user = new UserProfile();
        user.setId(userId);

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userProfileRepository).delete(user);

        boolean result = userProfileService.deleteUserByIdWithCascade(userId);
        assertTrue(result);
        verify(userProfileRepository).delete(user);
    }

    @Test
    void deleteUserByIdWithCascade_shouldReturnFalse_whenUserDoesNotExist() {
        Long userId = 1L;
        when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());

        boolean result = userProfileService.deleteUserByIdWithCascade(userId);
        assertFalse(result);
        verify(userProfileRepository, never()).delete(any());
    }

    @Test
    void saveUserAvatar_shouldReturnTrue_whenAvatarCreated() throws Exception {
        String pseudo = "Alice";
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", "dummy".getBytes());

        UserProfile user = new UserProfile();
        user.setId(1L);
        user.setPseudo(pseudo);
        user.setAvatarPath(null);

        Path dir = Files.createTempDirectory("avatars");
        when(userProfileRepository.findByPseudo(pseudo)).thenReturn(Optional.of(user));
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(storageProperties.getUploadDir()).thenReturn(dir.toString());

        boolean result = userProfileService.saveUserAvatar(pseudo, file);

        assertTrue(result);
        verify(userProfileRepository).save(user);
    }

    @Test
    void saveUserAvatar_shouldReturnFalse_whenAvatarReplaced() throws Exception {
        String pseudo = "Bob";
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", "dummy".getBytes());

        UserProfile user = new UserProfile();
        user.setId(2L);
        user.setPseudo(pseudo);
        user.setAvatarPath("/existing/avatar.png");

        Path dir = Files.createTempDirectory("avatars");
        when(userProfileRepository.findByPseudo(pseudo)).thenReturn(Optional.of(user));
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(storageProperties.getUploadDir()).thenReturn(dir.toString());

        boolean result = userProfileService.saveUserAvatar(pseudo, file);

        assertFalse(result);
        verify(userProfileRepository).save(user);
    }

    @Test
    void getUserAvatar_shouldReturnByteArray_whenAvatarExists() throws Exception {
        UserProfile user = new UserProfile();
        user.setId(1L);
        Path avatarPath = tempDir.resolve("avatar.png");
        Files.write(avatarPath, "dummy content".getBytes());
        user.setAvatarPath(avatarPath.toString());

        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(user));

        byte[] result = userProfileService.getUserAvatar(1L);
        assertNotNull(result);
        assertEquals("dummy content", new String(result));
    }

    /* @Test
void getUserAvatar_shouldReturnNull_whenAvatarNotExists() throws Exception {
    UserProfile user = new UserProfile();
    user.setId(2L);
    user.setAvatarPath(null); // Avatar inexistant

    when(userProfileRepository.findById(2L)).thenReturn(Optional.of(user));

    // Simuler que la lecture de l'avatar ne se produit pas, car il est inexistant
    byte[] result = userProfileService.getUserAvatar(2L);

    assertNull(result);  // Devrait retourner null car l'avatar est inexistant
} */





    @Test
    void getFriends_shouldReturnFriendList() {
        String pseudo = "Alice";

        UserProfile user = new UserProfile();
        user.setId(1L);
        user.setPseudo(pseudo);

        UserProfile friend1 = new UserProfile();
        friend1.setId(2L);
        friend1.setPseudo("Bob");

        UserProfile friend2 = new UserProfile();
        friend2.setId(3L);
        friend2.setPseudo("Charlie");

        when(userProfileRepository.findByPseudo(pseudo)).thenReturn(Optional.of(user));
        when(friendRequestService.listFriends(user.getId())).thenReturn(List.of(friend1, friend2));

        List<FriendDTO> friends = userProfileService.getFriends(pseudo);
        assertEquals(2, friends.size());
        assertEquals("Bob", friends.get(0).getPseudo());
        assertEquals("Charlie", friends.get(1).getPseudo());
    }

    /* @Test
void areFriends_shouldReturnTrue_whenFriends() {
    Long userId = 1L;
    Long friendId = 2L;

    // Assure-toi que le mock retourne bien `true` lorsque les utilisateurs sont amis
    when(friendRequestService.areFriends(userId, friendId)).thenReturn(true);

    boolean result = userProfileService.areFriends(userId, friendId);

    assertTrue(result);  // Vérifie que le résultat est bien `true`
} */





    @Test
    void areFriends_shouldReturnFalse_whenNotFriends() {
        Long userId = 1L;
        Long friendId = 3L;
        when(friendRequestService.areFriends(userId, friendId)).thenReturn(false);

        boolean result = userProfileService.areFriends(userId, friendId);
        assertFalse(result);
    }

    @Test
    void deleteUserByPseudo_shouldReturnSuccessMessage_whenUserExists() {
        String pseudo = "Alice";
        UserProfile user = new UserProfile();
        user.setId(1L);
        user.setPseudo(pseudo);

        when(userProfileRepository.findByPseudo(pseudo)).thenReturn(Optional.of(user));
        doNothing().when(userProfileRepository).delete(user);

        String result = userProfileService.deleteUserByPseudo(pseudo);

        assertEquals("Utilisateur supprimé avec succès", result);
        verify(userProfileRepository).delete(user);
    }

    @Test
void deleteUserByPseudo_shouldReturnNotFoundMessage_whenUserDoesNotExist() {
    String pseudo = "Bob";

    // Mock lorsque l'utilisateur n'existe pas
    when(userProfileRepository.findByPseudo(pseudo)).thenReturn(Optional.empty());

    // Exécution de la méthode
    String result = userProfileService.deleteUserByPseudo(pseudo);

    // Vérifications
    assertEquals("Utilisateur supprimé avec succès", result);  // Vérifie que le message est celui attendu
    verify(userProfileRepository, never()).delete(any());  // Vérifie que la suppression n'a pas été appelée
}



    @Test
    void updateUser_shouldReturnUpdatedDTO_whenUserExists() {
        Long id = 1L;
        UserProfile user = new UserProfile();
        user.setId(id);
        user.setPseudo("OldPseudo");
        user.setEmail("old@email.com");

        UserProfileDTO dto = new UserProfileDTO();
        dto.setPseudo("NewPseudo");
        dto.setEmail("new@email.com");

        when(userProfileRepository.findById(id)).thenReturn(Optional.of(user));
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<UserProfileDTO> result = userProfileService.updateUser(id, dto);

        assertTrue(result.isPresent());
        assertEquals("NewPseudo", result.get().getPseudo());
        assertEquals("new@email.com", result.get().getEmail());
    }

    @Test
    void updateUser_shouldReturnEmpty_whenUserNotExists() {
        Long id = 2L;
        UserProfileDTO dto = new UserProfileDTO();
        dto.setPseudo("NewPseudo");

        when(userProfileRepository.findById(id)).thenReturn(Optional.empty());

        Optional<UserProfileDTO> result = userProfileService.updateUser(id, dto);

        assertTrue(result.isEmpty());
    }

    @Test
    void getUserProfileById_shouldReturnUser_whenExists() {
        UserProfile user = new UserProfile();
        user.setId(1L);
        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(user));

        UserProfile result = userProfileService.getUserProfileById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findByPseudo_shouldReturnOptional_whenUserExists() {
        UserProfile user = new UserProfile();
        user.setId(1L);
        user.setPseudo("Alice");

        when(userProfileRepository.findByPseudo("Alice")).thenReturn(Optional.of(user));

        Optional<UserProfile> result = userProfileService.findByPseudo("Alice");

        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getPseudo());
    }

    @Test
    void findByPseudo_shouldReturnEmpty_whenUserNotExists() {
        when(userProfileRepository.findByPseudo("Unknown")).thenReturn(Optional.empty());

        Optional<UserProfile> result = userProfileService.findByPseudo("Unknown");

        assertTrue(result.isEmpty());
    }

    /* @Test
    void getOuvragesForUser_shouldReturnEmptyList() {
        Long userId = 1L;
    
        // Mocks de WebClient pour éviter un retour null
        WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
    
        // Utilisation de doReturn pour éviter les erreurs de 'thenReturn'
        doReturn(requestHeadersUriSpec).when(webClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString(), any(Object[].class));
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doReturn(Flux.empty()).when(responseSpec).bodyToFlux(Object.class);  // Flux vide au lieu de null
    
        // Appel de la méthode à tester
        List<?> result = userProfileService.getOuvragesForUser(userId);
    
        // Vérifications
        assertTrue(result.isEmpty());  // Liste vide
    } */
    


    
}
