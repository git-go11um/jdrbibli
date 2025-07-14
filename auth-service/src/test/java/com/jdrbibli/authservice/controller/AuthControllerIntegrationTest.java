package com.jdrbibli.authservice.controller;

import com.jdrbibli.authservice.AuthServiceApplication;
import com.jdrbibli.authservice.dto.ApiResponse;
import com.jdrbibli.authservice.dto.ChangePasswordProfileRequest;
import com.jdrbibli.authservice.dto.ChangePasswordRequest;
import com.jdrbibli.authservice.dto.InscriptionRequest;
import com.jdrbibli.authservice.dto.LoginRequest;
import com.jdrbibli.authservice.dto.PasswordResetRequest;
import com.jdrbibli.authservice.dto.ReponseProfileChange;
import com.jdrbibli.authservice.dto.UpdateUserRequest;
import com.jdrbibli.authservice.dto.UserResponseDTO;
import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.security.JwtService;
import com.jdrbibli.authservice.security.JwtTokenProvider;
import com.jdrbibli.authservice.service.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import static org.mockito.Mockito.*;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AuthServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "testuser")
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private AuthController controller;

    @BeforeEach
    public void setUp() {
        controller = new AuthController(userService, authenticationManager, jwtService, jwtTokenProvider);
    }

    @Test
    public void testRegister_shouldReturnOk() throws Exception {
        // Prépare la requête d'inscription
        InscriptionRequest request = new InscriptionRequest();
        request.setPseudo("testuser");
        request.setEmail("test@example.com");
        request.setMotDePasse("password");

        // Fake user retourné par le service
        User fakeUser = new User();
        fakeUser.setId(1L);
        fakeUser.setPseudo(request.getPseudo());
        fakeUser.setEmail(request.getEmail());
        fakeUser.setPassword("hashedpassword");

        // Fake DTO que toDTO devrait retourner
        UserResponseDTO fakeUserDTO = new UserResponseDTO(
                fakeUser.getId(),
                fakeUser.getPseudo(),
                fakeUser.getEmail(),
                Set.of("ROLE_USER"));

        // Mock des méthodes du service
        when(userService.inscrireNewUser(anyString(), anyString(), anyString())).thenReturn(fakeUser);
        when(userService.toDTO(any(User.class))).thenReturn(fakeUserDTO);

        // Effectuer la requête POST vers /auth/register (à adapter selon ton
        // controller)
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Vérifie que le service a bien été appelé
        verify(userService, times(1)).inscrireNewUser(anyString(), anyString(), anyString());
    }

    @Test
    public void testLogin_shouldReturnTokenAndUserDTO() throws Exception {
        // Prépare la requête login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPseudo("testuser");
        loginRequest.setMotDePasse("password");

        // User fake retourné par userService.getUserByPseudo()
        User fakeUser = new User();
        fakeUser.setId(1L);
        fakeUser.setPseudo(loginRequest.getPseudo());
        fakeUser.setPassword("hashedpassword");

        // UserResponseDTO fake retourné par userService.toDTO()
        UserResponseDTO fakeUserDTO = new UserResponseDTO(
                fakeUser.getId(),
                fakeUser.getPseudo(),
                "test@example.com",
                Set.of("ROLE_USER"));

        // Token JWT factice
        String fakeToken = "fake-jwt-token";

        // Mock des appels au service
        when(userService.getUserByPseudo(anyString())).thenReturn(fakeUser);
        when(userService.toDTO(any(User.class))).thenReturn(fakeUserDTO);
        when(jwtService.generateToken(anyString())).thenReturn(fakeToken);

        // Mock du AuthenticationManager (important pour éviter une vraie
        // authentification)
        Authentication fakeAuthentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(fakeAuthentication);

        // Effectue la requête POST /auth/login
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(fakeToken))
                .andExpect(jsonPath("$.user.pseudo").value(fakeUser.getPseudo()));

        // Vérifie les appels aux mocks
        verify(authenticationManager, times(1)).authenticate(any());
        verify(userService, times(1)).getUserByPseudo(loginRequest.getPseudo());
        verify(jwtService, times(1)).generateToken(fakeUser.getPseudo());
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testRefreshToken_shouldReturnNewToken() throws Exception {
        String oldToken = "old.jwt.token";
        String newToken = "new.jwt.token";
        String pseudo = "testuser";

        User fakeUser = new User();
        fakeUser.setPseudo(pseudo);

        UserResponseDTO fakeDto = new UserResponseDTO(
                1L,
                pseudo,
                "test@example.com",
                Collections.emptySet());

        when(jwtService.extractPseudo(oldToken)).thenReturn(pseudo);
        when(jwtService.generateToken(pseudo)).thenReturn(newToken);
        when(userService.getUserByPseudo(pseudo)).thenReturn(fakeUser);
        when(userService.toDTO(fakeUser)).thenReturn(fakeDto);

        mockMvc.perform(post("/auth/refresh")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + oldToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(newToken))
                .andExpect(jsonPath("$.user.pseudo").value(pseudo));
    }

    @Test
    public void testGetCurrentUser_shouldReturnUser() throws Exception {
        // Mock Authentication
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");

        // Mock User + DTO
        User fakeUser = new User();
        fakeUser.setPseudo("testuser");

        UserResponseDTO fakeDto = new UserResponseDTO(1L, "testuser", "test@example.com", Set.of("ROLE_USER"));

        // Mock service calls
        when(userService.getUserByPseudo("testuser")).thenReturn(fakeUser);
        when(userService.toDTO(fakeUser)).thenReturn(fakeDto);

        mockMvc.perform(get("/auth/me")
                .principal(auth)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pseudo").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testChangePassword_unitaire() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setNewPassword("NewPassword123!");
        request.setConfirmNewPassword("NewPassword123!");
        String connectedUserName = "testuser";

        // Mock du service
        doNothing().when(userService).changePassword(eq(connectedUserName), any());

        // Appel direct de la méthode du controller
        ResponseEntity<?> response = controller.changePassword(request, () -> connectedUserName);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Mot de passe changé avec succès", ((Map<?, ?>) response.getBody()).get("message"));

        // Vérifier interaction
        verify(userService, times(1)).changePassword(eq(connectedUserName), any());
    }

    @Test
    void testValidateResetCode_shouldReturnOkWhenCodeIsValid() {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setPseudo("testuser");
        request.setCode("ABC123");

        // On simule que le code est valide
        when(userService.validateResetCode("testuser", "ABC123")).thenReturn(true);

        ResponseEntity<?> response = controller.validateResetCode(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Code valide", ((Map<?, ?>) response.getBody()).get("message"));

        verify(userService, times(1)).validateResetCode("testuser", "ABC123");
    }

    @Test
    void testValidateResetCode_shouldReturnBadRequestWhenCodeIsInvalid() {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setPseudo("testuser");
        request.setCode("WRONG");

        // On simule que le code est invalide
        when(userService.validateResetCode("testuser", "WRONG")).thenReturn(false);

        ResponseEntity<?> response = controller.validateResetCode(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Code invalide", ((Map<?, ?>) response.getBody()).get("message"));

        verify(userService, times(1)).validateResetCode("testuser", "WRONG");
    }

    @Test
    void testValidateResetCode_shouldReturnInternalServerErrorOnException() {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setPseudo("testuser");
        request.setCode("ANY");

        // On simule une exception dans le service
        when(userService.validateResetCode("testuser", "ANY")).thenThrow(new RuntimeException("Boom"));

        ResponseEntity<?> response = controller.validateResetCode(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        String message = (String) ((Map<?, ?>) response.getBody()).get("message");
        assertTrue(message.startsWith("Erreur serveur"));

        verify(userService, times(1)).validateResetCode("testuser", "ANY");
    }

    @Test
    void testResetPassword_shouldReturnOk() {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setPseudo("testuser");
        request.setCode("ABC123");
        request.setNewPassword("NewPassword123!");

        // On simule que tout se passe bien (la méthode ne retourne rien)
        doNothing().when(userService).resetPassword("testuser", "ABC123", "NewPassword123!");

        ResponseEntity<?> response = controller.resetPassword(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Mot de passe réinitialisé avec succès", ((Map<?, ?>) response.getBody()).get("message"));

        verify(userService, times(1)).resetPassword("testuser", "ABC123", "NewPassword123!");
    }

    @Test
    void testResetPassword_shouldReturnInternalServerErrorOnException() {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setPseudo("testuser");
        request.setCode("ABC123");
        request.setNewPassword("NewPassword123!");

        // On simule une exception lancée par le service
        doThrow(new RuntimeException("Boom")).when(userService)
                .resetPassword("testuser", "ABC123", "NewPassword123!");

        ResponseEntity<?> response = controller.resetPassword(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        String message = (String) ((Map<?, ?>) response.getBody()).get("message");
        assertTrue(message.startsWith("Erreur serveur"));

        verify(userService, times(1)).resetPassword("testuser", "ABC123", "NewPassword123!");
    }

    @Test
    void testRequestPasswordReset_shouldReturnOk() throws Exception {
        String pseudo = "testuser";

        doNothing().when(userService).requestPasswordReset(pseudo);

        Map<String, String> body = Map.of("pseudo", pseudo);

        ResponseEntity<?> response = controller.requestPasswordReset(body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Code de réinitialisation envoyé", ((Map<?, ?>) response.getBody()).get("message"));

        verify(userService, times(1)).requestPasswordReset(pseudo);
    }

    @Test
    void testRequestPasswordReset_shouldHandleException() throws Exception {
        String pseudo = "testuser";

        doThrow(new RuntimeException("Boom")).when(userService).requestPasswordReset(pseudo);

        Map<String, String> body = Map.of("pseudo", pseudo);

        ResponseEntity<?> response = controller.requestPasswordReset(body);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        String message = (String) ((Map<?, ?>) response.getBody()).get("message");
        assertTrue(message.contains("Erreur serveur"));
        assertTrue(message.contains("Boom"));

        verify(userService, times(1)).requestPasswordReset(pseudo);
    }

    @Test
    void testDeleteUser_shouldReturnOk() {
        String pseudo = "testuser";
        String bearerToken = "Bearer fake.jwt.token";

        // Mock du pseudo extrait du token
        when(jwtService.extractPseudo("fake.jwt.token")).thenReturn(pseudo);

        // Mock de l’utilisateur trouvé
        User fakeUser = new User();
        fakeUser.setId(1L);
        fakeUser.setPseudo(pseudo);
        when(userService.getUserByPseudo(pseudo)).thenReturn(fakeUser);

        doNothing().when(userService).deleteUserById(fakeUser.getId());

        // Appel direct du controller
        ResponseEntity<?> response = controller.deleteUser(pseudo, bearerToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Utilisateur supprimé avec succès.", response.getBody());

        verify(jwtService, times(1)).extractPseudo("fake.jwt.token");
        verify(userService, times(1)).getUserByPseudo(pseudo);
        verify(userService, times(1)).deleteUserById(fakeUser.getId());
    }

    @Test
    void testDeleteUser_shouldReturnForbidden() {
        String pseudo = "testuser";
        String bearerToken = "Bearer fake.jwt.token";

        // Mock: le pseudo extrait est différent
        when(jwtService.extractPseudo("fake.jwt.token")).thenReturn("otheruser");

        ResponseEntity<?> response = controller.deleteUser(pseudo, bearerToken);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Vous ne pouvez supprimer que votre propre compte.", response.getBody());

        verify(jwtService, times(1)).extractPseudo("fake.jwt.token");
        verify(userService, never()).getUserByPseudo(anyString());
        verify(userService, never()).deleteUserById(anyLong());
    }

    @Test
    void testDeleteUser_shouldReturnNotFound() {
        String pseudo = "testuser";
        String bearerToken = "Bearer fake.jwt.token";

        when(jwtService.extractPseudo("fake.jwt.token")).thenReturn(pseudo);

        // Mock: getUserByPseudo retourne null
        when(userService.getUserByPseudo(pseudo)).thenReturn(null);

        ResponseEntity<?> response = controller.deleteUser(pseudo, bearerToken);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Utilisateur non trouvé.", response.getBody());

        verify(jwtService, times(1)).extractPseudo("fake.jwt.token");
        verify(userService, times(1)).getUserByPseudo(pseudo);
        verify(userService, never()).deleteUserById(anyLong());
    }

    @Test
    void testUpdateUserProfile_shouldReturnOk() {
        // Préparer la requête
        UpdateUserRequest request = new UpdateUserRequest();
        request.setPseudo("newPseudo");
        request.setEmail("new@example.com");

        // Simuler un UserDetails avec l'ID
        User fakeUserDetails = new User();
        fakeUserDetails.setId(1L);

        // Mock du service : il ne renvoie rien, donc doNothing suffit
        doNothing().when(userService).updateUserProfile(eq(1L), eq("newPseudo"), eq("new@example.com"));

        // Appel direct
        ResponseEntity<ReponseProfileChange> response = controller.updateUserProfile(request, fakeUserDetails);

        // Vérifier
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Profil mis à jour avec succès", response.getBody().getMessage());

        verify(userService, times(1)).updateUserProfile(eq(1L), eq("newPseudo"), eq("new@example.com"));
    }

    @Test
    void testUpdateUserProfile_shouldReturnBadRequest() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setPseudo("badPseudo");
        request.setEmail("bad@example.com");

        User fakeUserDetails = new User();
        fakeUserDetails.setId(1L);

        // Simuler que le service throw une exception
        doThrow(new IllegalArgumentException("Pseudo déjà utilisé")).when(userService)
                .updateUserProfile(eq(1L), eq("badPseudo"), eq("bad@example.com"));

        ResponseEntity<ReponseProfileChange> response = controller.updateUserProfile(request, fakeUserDetails);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Pseudo déjà utilisé", response.getBody().getMessage());

        verify(userService, times(1)).updateUserProfile(eq(1L), eq("badPseudo"), eq("bad@example.com"));
    }

    @Test
    void testChangeProfilePassword_shouldReturnOk() {
        // Préparer la requête
        ChangePasswordProfileRequest request = new ChangePasswordProfileRequest();
        request.setCurrentPassword("OldPassword123!");
        request.setNewPassword("NewPassword123!");
        request.setConfirmNewPassword("NewPassword123!");

        String email = "test@example.com";
        String newToken = "new.jwt.token";

        // Mock : le service ne renvoie rien (void)
        doNothing().when(userService).changeProfilePassword(eq(email), any(ChangePasswordProfileRequest.class));

        // Mock : génération du token
        when(jwtTokenProvider.createToken(email)).thenReturn(newToken);

        // Simuler le Principal
        Principal principal = () -> email;

        // Appel direct
        ResponseEntity<?> response = controller.changeProfilePassword(request, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        assertEquals("Mot de passe mis à jour avec succès", apiResponse.getMessage());
        assertTrue(apiResponse.isSuccess());
        assertEquals(newToken, apiResponse.getToken());

        verify(userService, times(1)).changeProfilePassword(eq(email), any(ChangePasswordProfileRequest.class));
        verify(jwtTokenProvider, times(1)).createToken(email);
    }

    @Test
    void testChangeProfilePassword_shouldReturnBadRequest() {
        ChangePasswordProfileRequest request = new ChangePasswordProfileRequest();
        request.setCurrentPassword("OldPassword123!");
        request.setNewPassword("NewPassword123!");
        request.setConfirmNewPassword("NewPassword123!");

        String email = "test@example.com";

        // Simuler une exception
        doThrow(new RuntimeException("Erreur technique")).when(userService)
                .changeProfilePassword(eq(email), any(ChangePasswordProfileRequest.class));

        Principal principal = () -> email;

        ResponseEntity<?> response = controller.changeProfilePassword(request, principal);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        assertEquals("Erreur lors de la mise à jour du mot de passe", apiResponse.getMessage());
        assertFalse(apiResponse.isSuccess());
        assertNull(apiResponse.getToken());

        verify(userService, times(1)).changeProfilePassword(eq(email), any(ChangePasswordProfileRequest.class));
        // Le token ne doit pas être généré en cas d'erreur
        verify(jwtTokenProvider, never()).createToken(anyString());
    }

}
