package com.jdrbibli.authservice.service;

import com.jdrbibli.authservice.client.AuditClient;
import com.jdrbibli.authservice.dto.*;
import com.jdrbibli.authservice.entity.Role;
import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.exception.BadCredentialsException;
import com.jdrbibli.authservice.exception.UserNotFoundException;
import com.jdrbibli.authservice.repository.PasswordResetTokenRepository;
import com.jdrbibli.authservice.repository.UserRepository;
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
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final RestTemplate restTemplate;

    @Value("${app.user-service-url}")
    private String userServiceUrl;

    @Autowired
    private AuditClient auditClient;

    @Autowired
    public UserService(RestTemplate restTemplate,
            @Value("${app.user-service-url}") String userServiceUrl,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JavaMailSender mailSender,
            JwtTokenProvider jwtTokenProvider,
            PasswordResetTokenRepository passwordResetTokenRepository) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
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

    // -------------------------------------------------------
    // INSCRIPTION
    // -------------------------------------------------------
    @Override
    @Transactional
    public User inscrireNewUser(String pseudo, String email, String password) {
        PasswordValidator.validate(password);
        String hashedPassword = passwordEncoder.encode(password);
        User newUser = new User(null, pseudo, email, hashedPassword, new HashSet<>(), null, null);
        User savedUser = userRepository.save(newUser);

        try {
            createUserProfile(savedUser.getId(), savedUser.getPseudo(), savedUser.getEmail());
        } catch (Exception e) {
            System.err.println("⚠️ Échec création profil user-service pour pseudo="
                    + savedUser.getPseudo() + " : " + e.getMessage());
        }

        auditClient.logEvent("auth-service", "USER_CREATED",
                "Utilisateur créé: " + savedUser.getPseudo() + " (id:" + savedUser.getId() + ")");
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

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        auditClient.logEvent("auth-service", "USER_DELETED", "Utilisateur supprimé avec id: " + id);
    }

    @Transactional
    public boolean deleteUserWithCascade(Long userId) {
        String url = userServiceUrl + "/api/users/" + userId + "/cascade";
        try {
            restTemplate.delete(url);
            System.out.println("✅ Suppression cascade réussie dans user-service pour id=" + userId);
        } catch (HttpClientErrorException.NotFound e) {
            System.err.println("Profil utilisateur " + userId + " inexistant côté user-service.");
        } catch (Exception e) {
            System.err
                    .println("Erreur lors de la suppression dans user-service (id=" + userId + ") : " + e.getMessage());
            return false;
        }

        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            System.out.println("✅ Compte utilisateur " + userId + " supprimé côté auth-service.");
            auditClient.logEvent("auth-service", "USER_DELETED", "Utilisateur supprimé avec id: " + userId);
            return true;
        } else {
            System.err.println("Compte utilisateur " + userId + " déjà supprimé côté auth-service.");
            return false;
        }
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID : " + userId));

        userRepository.delete(user);

        String url = userServiceUrl + "/api/users/" + userId + "/cascade";
        try {
            restTemplate.delete(url);
            System.out.println("✅ Suppression cascade réussie dans user-service pour id=" + userId);
        } catch (Exception e) {
            System.err.println(
                    "⚠️ Erreur lors de la suppression dans user-service (id=" + userId + ") : " + e.getMessage());
        }

        auditClient.logEvent("auth-service", "USER_DELETED", "Utilisateur supprimé avec id: " + userId);
    }

    @Override
    public User getUserByPseudo(String pseudo) {
        return userRepository.findByPseudo(pseudo)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec le pseudo : " + pseudo));
    }

    // -------------------------------------------------------
    // PASSWORD MANAGEMENT
    // -------------------------------------------------------

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

        auditClient.logEvent(
                "auth-service",
                "PASSWORD_CHANGED",
                "Mot de passe modifié pour id: " + user.getId() + " | pseudo: " + user.getPseudo());
    }

    @Override
    public void requestPasswordReset(String pseudo) {
        User user = getUserByPseudo(pseudo);
        String code = generateResetCode(6);
        long expirationTime = System.currentTimeMillis() + 24 * 60 * 60 * 1000;

        user.setResetCode(code);
        user.setResetPasswordCodeExpiration(expirationTime);
        userRepository.save(user);

        sendResetPasswordEmail(user.getEmail(), code);
        auditClient.logEvent("auth-service", "PASSWORD_RESET_REQUESTED", "Réinitialisation demandée pour " + pseudo);
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

        return storedCode != null && storedCode.equalsIgnoreCase(code)
                && expiration != null && expiration > System.currentTimeMillis();
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

        auditClient.logEvent(
                "auth-service",
                "PASSWORD_RESET",
                "Mot de passe réinitialisé via 'mot de passe oublié' pour id: " + user.getId() + " | pseudo: "
                        + user.getPseudo());
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
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void changeProfilePassword(String userPseudo,
            com.jdrbibli.authservice.dto.ChangePasswordProfileRequest request) {
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

        auditClient.logEvent(
                "auth-service",
                "PASSWORD_CHANGED",
                "Mot de passe modifié manuellement pour id: " + user.getId() + " | pseudo: " + user.getPseudo());
    }

    // -------------------------------------------------------
    // UPDATE PROFILE
    // -------------------------------------------------------
    @Override
    public ReponseProfileChange updateUserProfile(Long userId, String newPseudo, String newEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé"));

        String oldPseudo = user.getPseudo();
        String oldEmail = user.getEmail();
        boolean pseudoChanged = false;
        boolean emailChanged = false;

        if (newPseudo != null && !newPseudo.trim().isEmpty() && !newPseudo.equals(oldPseudo)) {
            user.setPseudo(newPseudo);
            pseudoChanged = true;
        }

        if (newEmail != null && !newEmail.trim().isEmpty() && !newEmail.equals(oldEmail)) {
            user.setEmail(newEmail);
            emailChanged = true;
        }

        if (!pseudoChanged && !emailChanged) {
            return new ReponseProfileChange("Aucun changement détecté.", null);
        }

        userRepository.save(user);
        String newToken = jwtTokenProvider.createToken(user.getPseudo());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(newToken); // ✅ pour éviter les 403

            UserProfileDTO body = new UserProfileDTO(user.getId(), user.getPseudo(), user.getEmail());
            HttpEntity<UserProfileDTO> request = new HttpEntity<>(body, headers);

            String updateUrl = userServiceUrl + "/api/users/" + user.getId(); // ✅ fixe pour la mise à jour
            restTemplate.exchange(updateUrl, HttpMethod.PUT, request, Map.class);

        } catch (Exception e) {
            System.err.println("Erreur synchronisation user-service pour " + user.getPseudo() + " : " + e.getMessage());
            e.printStackTrace();
        }

        StringBuilder details = new StringBuilder("Modification du profil pour id: " + userId);
        if (pseudoChanged) {
            details.append(" | pseudo: '").append(oldPseudo).append("' → '").append(newPseudo).append("'");
        }
        if (emailChanged) {
            details.append(" | email: '").append(oldEmail).append("' → '").append(newEmail).append("'");
        }
        auditClient.logEvent("auth-service", "USER_UPDATED", details.toString());

        return new ReponseProfileChange("Profil mis à jour avec succès.", newToken);
    }

    // -------------------------------------------------------
    // CREATE USER PROFILE (fix)
    // -------------------------------------------------------
    private void createUserProfile(Long id, String pseudo, String email) {
        // ✅ appel direct à /api/users pour correspondre au mapping du user-service
        String url = userServiceUrl + "/api/users";

        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("pseudo", pseudo);
        body.put("email", email);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(url, request, Void.class);
        } catch (Exception e) {
            System.err.println("Erreur création profil user-service pour pseudo=" + pseudo + " : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------
    // OTHER UTILITIES
    // -------------------------------------------------------
    public void deletePasswordResetTokens(Long userId) {
        passwordResetTokenRepository.deleteByUserId(userId);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur avec id " + id + " non trouvé"));
    }

    @Override
    public UserResponseDTO toDTO(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());
        return new UserResponseDTO(user.getId(), user.getPseudo(), user.getEmail(), roleNames);
    }
}
