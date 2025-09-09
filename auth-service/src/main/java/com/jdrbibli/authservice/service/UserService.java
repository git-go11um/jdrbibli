package com.jdrbibli.authservice.service;

import com.jdrbibli.authservice.dto.ChangePasswordProfileRequest;
import com.jdrbibli.authservice.dto.ChangePasswordRequest;
import com.jdrbibli.authservice.dto.ReponseProfileChange;
import com.jdrbibli.authservice.dto.UserProfileDTO;
import com.jdrbibli.authservice.dto.UserResponseDTO;
import com.jdrbibli.authservice.entity.Role;
import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.exception.BadCredentialsException;
import com.jdrbibli.authservice.exception.UserNotFoundException;
import com.jdrbibli.authservice.repository.UserRepository;
import com.jdrbibli.authservice.repository.PasswordResetTokenRepository;
import com.jdrbibli.authservice.security.JwtTokenProvider;
import com.jdrbibli.authservice.util.PasswordValidator;

import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate = new RestTemplate();
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${app.user-service-url}")
    private String userServiceUrl;

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

    @PostConstruct
    public void init() {
        System.out.println("userServiceUrl = " + userServiceUrl);
    }

    @Override
    public User inscrireNewUser(String pseudo, String email, String password) {
        PasswordValidator.validate(password);
        String hashedPassword = passwordEncoder.encode(password);
        User newUser = new User(null, pseudo, email, hashedPassword, new HashSet<>(), null, null);
        User savedUser = userRepository.save(newUser);

        try {
            createUserProfile(savedUser.getId(), savedUser.getPseudo(), savedUser.getEmail());
        } catch (Exception e) {
            System.err.println(
                    "Échec création profil user-service pour pseudo=" + savedUser.getPseudo() + " : " + e.getMessage());
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur avec id " + userId + " non trouvé"));

        deletePasswordResetTokens(userId);
        userRepository.deleteById(userId);

        try {
            String url = userServiceUrl + "/by-pseudo/" + user.getPseudo();
            restTemplate.delete(url);
            System.out.println("✅ Profil user-service supprimé pour pseudo=" + user.getPseudo());
        } catch (Exception e) {
            System.err.println(
                    "⚠️ Erreur suppression user-service pour pseudo=" + user.getPseudo() + " : " + e.getMessage());
            e.printStackTrace();
        }
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

    @Override
    public void requestPasswordReset(String pseudo) {
        User user = getUserByPseudo(pseudo);
        String code = generateResetCode(6);
        long expirationTime = System.currentTimeMillis() + 24 * 60 * 60 * 1000;

        user.setResetCode(code);
        user.setResetPasswordCodeExpiration(expirationTime);
        userRepository.save(user);

        System.out.println("Code généré : " + code);
        sendResetPasswordEmail(user.getEmail(), code);
    }

    private String generateResetCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }

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
        System.out.println("Résultat final validateResetCode=" + isValid);

        return isValid;
    }

    @Override
    public void resetPassword(String pseudo, String resetCode, String newPassword) {
        User user = getUserByPseudo(pseudo);

        if (!validateResetCode(pseudo, resetCode)) {
            throw new IllegalArgumentException("Le code de réinitialisation est invalide ou a expiré.");
        }

        PasswordValidator.validate(newPassword);
        user.setPassword(passwordEncoder.encode(newPassword));

        user.setResetCode(null);
        user.setResetPasswordCodeExpiration(null);

        userRepository.save(user);
    }

    public UserResponseDTO toDTO(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());
        return new UserResponseDTO(user.getId(), user.getPseudo(), user.getEmail(), roleNames);
    }

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
        // Récupération du user dans auth_db
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé"));

        String oldPseudo = user.getPseudo();
        boolean pseudoChanged = false;
        boolean emailChanged = false;

        if (newPseudo != null && !newPseudo.trim().isEmpty() && !newPseudo.equals(oldPseudo)) {
            user.setPseudo(newPseudo);
            pseudoChanged = true;
        }

        if (newEmail != null && !newEmail.trim().isEmpty() && !newEmail.equals(user.getEmail())) {
            user.setEmail(newEmail);
            emailChanged = true;
        }

        if (!pseudoChanged && !emailChanged) {
            return new ReponseProfileChange("Aucun changement détecté.", null);
        }

        userRepository.save(user);
        String newToken = jwtTokenProvider.createToken(user.getPseudo());

        // Synchronisation côté user-service
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-User-Name", user.getPseudo());

            UserProfileDTO body = new UserProfileDTO(user.getId(), user.getPseudo(), user.getEmail());
            HttpEntity<UserProfileDTO> request = new HttpEntity<>(body, headers);

            // PUT direct vers /api/users/{id} sans rechercher l'ancien pseudo
            String updateUrl = userServiceUrl + "/" + user.getId();
            restTemplate.exchange(updateUrl, HttpMethod.PUT, request, Map.class);

        } catch (Exception e) {
            System.err.println("Erreur synchronisation user-service pour " + user.getPseudo() + " : " + e.getMessage());
            e.printStackTrace();
        }

        return new ReponseProfileChange("Profil mis à jour avec succès.", newToken);
    }

    @Override
    public void changeProfilePassword(String userPseudo, ChangePasswordProfileRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("Les deux mots de passe ne correspondent pas");
        }

        PasswordValidator.validate(request.getNewPassword());

        User user = userRepository.findByPseudo(userPseudo)
                .orElseThrow(
                        () -> new UserNotFoundException("Utilisateur avec le pseudo " + userPseudo + " non trouvé"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Mot de passe actuel incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private void createUserProfile(Long id, String pseudo, String email) {
        String url = userServiceUrl;

        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("pseudo", pseudo);
        body.put("email", email);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwtTokenProvider.createToken(pseudo));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(url, request, Void.class);
        } catch (HttpClientErrorException.Unauthorized e) {
            System.err.println("401 Unauthorized pour pseudo=" + pseudo);
        } catch (Exception e) {
            System.err.println("Erreur création profil user-service pour pseudo=" + pseudo + " : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deletePasswordResetTokens(Long userId) {
        passwordResetTokenRepository.deleteByUserId(userId);
    }

    public void testUserServiceConnection() {
        String url = userServiceUrl;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
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
