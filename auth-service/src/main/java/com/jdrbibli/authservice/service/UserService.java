package com.jdrbibli.authservice.service;

import com.jdrbibli.authservice.dto.ChangePasswordProfileRequest;
import com.jdrbibli.authservice.dto.ChangePasswordRequest;
import com.jdrbibli.authservice.dto.ReponseProfileChange;
import com.jdrbibli.authservice.dto.UserResponseDTO;
import com.jdrbibli.authservice.entity.Role;
import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.exception.BadCredentialsException;
import com.jdrbibli.authservice.exception.UserNotFoundException;
import com.jdrbibli.authservice.repository.UserRepository;
import com.jdrbibli.authservice.security.JwtTokenProvider;
import com.jdrbibli.authservice.util.PasswordValidator;
import com.jdrbibli.authservice.repository.PasswordResetTokenRepository;

import jakarta.mail.internet.MimeMessage;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate = new RestTemplate();

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JavaMailSender mailSender,
            JwtTokenProvider jwtTokenProvider,
            PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Override
    public User inscrireNewUser(String pseudo, String email, String password) {
        PasswordValidator.validate(password);
        String hashedPassword = passwordEncoder.encode(password);
        User newUser = new User(null, pseudo, email, hashedPassword, new HashSet<>(), null, null);
        // Sauvegarde local auth-service
        User savedUser = userRepository.save(newUser);

        // Tentative de création non-bloquante du profil côté user-service
        try {
            createUserProfile(savedUser.getPseudo(), savedUser.getEmail());
        } catch (Exception e) {
            // Ne pas empêcher l'inscription si user-service est down — journaliser l'erreur
            System.err.println("Échec création profil user-service pour pseudo=" + savedUser.getPseudo()
                    + " : " + e.getMessage());
            e.printStackTrace();
        }

        return savedUser;
    }

    @Override
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Mot de passe incorrect");
        }
        return user;
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Utilisateur avec id " + userId + " non trouvé");
        }

        // Supprimer les tokens de réinitialisation associés à l'utilisateur
        deletePasswordResetTokens(userId);

        // Supprimer l'utilisateur de la base de données
        userRepository.deleteById(userId);
    }

    public void deletePasswordResetTokens(Long userId) {
        // Supprimer les tokens associés à l'utilisateur
        passwordResetTokenRepository.deleteByUserId(userId);
    }

    @Override
    public User getUserByPseudo(String pseudo) {
        return userRepository.findByPseudo(pseudo)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec le pseudo : " + pseudo));
    }

    @Override
    public void changePassword(String userEmail, ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("Les deux mots de passe ne correspondent pas");
        }
        PasswordValidator.validate(request.getNewPassword());
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur avec l'email " + userEmail + " non trouvé"));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * Envoie un code de réinitialisation (court) et définit son expiration
     */
    @Override
    public void requestPasswordReset(String pseudo) {
        User user = getUserByPseudo(pseudo);

        // Générer un code de 6 caractères alphanumériques
        String code = generateResetCode(6);

        // Expiration dans 24h
        long expirationTime = System.currentTimeMillis() + 24 * 60 * 60 * 1000;

        user.setResetCode(code);
        user.setResetPasswordCodeExpiration(expirationTime);

        userRepository.save(user);

        System.out.println("Code généré : " + code);

        sendResetPasswordEmail(user.getEmail(), code);
    }

    // Méthode utilitaire pour générer un code alphanumérique
    private String generateResetCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }

    /**
     * Vérifie si le code fourni correspond et n'a pas expiré
     */
    @Override
    public boolean validateResetCode(String pseudo, String code) {
        User user = getUserByPseudo(pseudo);
        String storedCode = user.getResetCode();
        Long expiration = user.getResetPasswordCodeExpiration();

        boolean isValid = storedCode != null
                && storedCode.equalsIgnoreCase(code)
                && expiration != null
                && expiration > System.currentTimeMillis();

        System.out.println("storedCode='" + storedCode + "' | code='" + code + "'");
        System.out.println("lengths: " + storedCode.length() + " / " + code.length());
        System.out.println("Résultat final validateResetCode=" + isValid);

        return isValid;
    }

    /**
     * Réinitialise le mot de passe après validation du code
     */
    @Override
    public void resetPassword(String pseudo, String resetCode, String newPassword) {
        User user = getUserByPseudo(pseudo);

        if (!validateResetCode(pseudo, resetCode)) {
            throw new IllegalArgumentException("Le code de réinitialisation est invalide ou a expiré.");
        }

        PasswordValidator.validate(newPassword);

        user.setPassword(passwordEncoder.encode(newPassword));

        // Invalider le code après usage
        user.setResetCode(null);
        user.setResetPasswordCodeExpiration(null);

        userRepository.save(user);
    }

    /**
     * Transforme User → DTO
     */
    public UserResponseDTO toDTO(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());
        return new UserResponseDTO(user.getId(), user.getPseudo(), user.getEmail(), roleNames);
    }

    /**
     * Méthode utilitaire pour envoyer l'email
     */
    private void sendResetPasswordEmail(String email, String code) {
        String subject = "Réinitialisation de votre mot de passe";
        String message = "Voici votre code de réinitialisation : " + code + "\nCe code expire dans 24h.";

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
            helper.setFrom("no-reply@jdrbibli.com");
            helper.setTo(email);
            helper.setSubject(subject);
            mimeMessage.setText(message, "utf-8", "plain");
            mailSender.send(mimeMessage);
            System.out.println("Email envoyé à : " + email);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }

    @Override
    public ReponseProfileChange updateUserProfile(Long userId, String newPseudo, String newEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé"));

        String oldPseudo = user.getPseudo();
        boolean pseudoChanged = false;
        boolean emailChanged = false;

        if (newPseudo != null && !newPseudo.trim().isEmpty() && !newPseudo.equals(oldPseudo)) {
            System.out.println("🔄 Pseudo modifié : " + oldPseudo + " → " + newPseudo);
            user.setPseudo(newPseudo);
            pseudoChanged = true;
        }

        if (newEmail != null && !newEmail.trim().isEmpty() && !newEmail.equals(user.getEmail())) {
            System.out.println("🔄 Email modifié : " + user.getEmail() + " → " + newEmail);
            user.setEmail(newEmail);
            emailChanged = true;
        }

        if (!pseudoChanged && !emailChanged) {
            System.out.println("ℹ️ Aucun changement détecté, rien à mettre à jour.");
            return new ReponseProfileChange("Aucun changement détecté.", null);
        }

        // Sauvegarde dans auth-db
        userRepository.save(user);
        System.out.println("✅ Utilisateur mis à jour dans auth-db : " + user.getPseudo());

        // Générer un nouveau token JWT
        String newToken = jwtTokenProvider.createToken(user.getPseudo());

        // Synchronisation avec user-service
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // AJOUT DU JWT
            headers.set("Authorization", "Bearer " + newToken);

            Map<String, String> body = new HashMap<>();
            body.put("pseudo", user.getPseudo());
            body.put("email", user.getEmail());

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            // 1️⃣ Rechercher le profil dans user-service avec l'ancien pseudo
            String searchUrl = "http://localhost:8082/api/users/search?pseudo=" + oldPseudo;
            System.out.println("➡️ Appel GET " + searchUrl);

            ResponseEntity<Map> searchResponse = restTemplate.getForEntity(searchUrl, Map.class);
            System.out.println("⬅️ Réponse user-service : " + searchResponse.getStatusCode());

            if (searchResponse.getStatusCode().is2xxSuccessful() && searchResponse.getBody() != null) {
                Object profileIdObj = searchResponse.getBody().get("id");

                if (profileIdObj != null) {
                    String updateUrl = "http://localhost:8082/api/users/" + profileIdObj;
                    System.out.println("➡️ Appel PUT " + updateUrl + " avec body=" + body);
                    restTemplate.exchange(updateUrl, HttpMethod.PUT, request, Map.class);
                    System.out.println("✅ Profil user-service mis à jour : " + user.getPseudo());
                } else {
                    System.out.println("⚠️ Profil trouvé mais pas d'ID → création forcée.");
                    createUserProfile(user.getPseudo(), user.getEmail());
                }
            } else {
                // 2️⃣ Profil non existant → création
                System.out.println("⚠️ Aucun profil trouvé → création");
                createUserProfile(user.getPseudo(), user.getEmail());
            }

        } catch (Exception e) {
            System.err
                    .println("❌ Erreur synchronisation user-service pour " + user.getPseudo() + " : " + e.getMessage());
            e.printStackTrace();
        }

        return new ReponseProfileChange("Profil mis à jour avec succès.", newToken);
    }

    @Override
    public void changeProfilePassword(String userPseudo, ChangePasswordProfileRequest request) {
        // Vérifier que le nouveau mot de passe correspond à la confirmation
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("Les deux mots de passe ne correspondent pas");
        }

        // Valider la complexité du mot de passe
        PasswordValidator.validate(request.getNewPassword());

        // Récupérer l'utilisateur avec l'email
        User user = userRepository.findByPseudo(userPseudo)
                .orElseThrow(
                        () -> new UserNotFoundException("Utilisateur avec le pseudo " + userPseudo + " non trouvé"));

        // Vérifier que le mot de passe actuel est correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Mot de passe actuel incorrect");
        }

        // Mettre à jour le mot de passe (le hacher avant d'enregistrer)
        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword()); // Encoder le nouveau mot de passe
        user.setPassword(encodedNewPassword);

        // Sauvegarder l'utilisateur avec le nouveau mot de passe
        userRepository.save(user);
    }

    private void createUserProfile(String pseudo, String email) {
        String url = "http://localhost:8082/api/users"; // endpoint user-service
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> body = new HashMap<>();
        body.put("pseudo", pseudo);
        body.put("email", email);

        restTemplate.postForEntity(url, body, Void.class);
    }

    public void testUserServiceConnection() {
        String url = "http://localhost:8082/api/users"; // URL user-service
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // JSON minimal pour créer un UserProfile
        String jsonBody = "{ \"pseudo\": \"testHttp\", \"email\": \"testHttp@user.com\" }";
        HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            System.out.println("Réponse user-service : " + response.getStatusCode() + " | " + response.getBody());
        } catch (Exception e) {
            System.err.println("Erreur lors de l'appel à user-service : " + e.getMessage());
            e.printStackTrace();
        }
    }

}
