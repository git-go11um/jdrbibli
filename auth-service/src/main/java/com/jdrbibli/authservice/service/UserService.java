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

/**
 * Service principal de gestion des utilisateurs.
 * 
 * Gère l'inscription, la connexion, la suppression (avec ou sans cascade),
 * la gestion des mots de passe et la réinitialisation.
 * 
 * Interagit avec :
 * - {@link UserRepository} pour la persistance des utilisateurs
 * - {@link PasswordEncoder} pour le hachage des mots de passe
 * - {@link JwtTokenProvider} pour la gestion des tokens JWT
 * - {@link AuditClient} pour tracer les événements
 * - {@link RestTemplate} pour communiquer avec le user-service
 */
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

    /**
     * Constructeur injectant les dépendances essentielles.
     */
    @Autowired
    public UserService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JavaMailSender mailSender,
            JwtTokenProvider jwtTokenProvider,
            PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Méthode exécutée après l’initialisation du bean Spring.
     * Permet de vérifier la valeur de {@code userServiceUrl}.
     */
    @PostConstruct
    public void init() {
        System.out.println("userServiceUrl = " + userServiceUrl);
    }

    /**
     * Inscription d’un nouvel utilisateur.
     *
     * @param pseudo   Pseudo choisi par l’utilisateur
     * @param email    Email de l’utilisateur
     * @param password Mot de passe en clair, qui sera validé puis haché
     * @return l’utilisateur sauvegardé
     * @throws IllegalArgumentException si le mot de passe ne respecte pas les
     *                                  règles
     */
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
            System.err.println(
                    "Échec création profil user-service pour pseudo=" + savedUser.getPseudo() + " : " + e.getMessage());
            e.printStackTrace();
        }

        auditClient.logEvent("auth-service", "USER_CREATED",
                "Utilisateur créé: " + savedUser.getPseudo() + " (id:" + savedUser.getId() + ")");
        return savedUser;
    }

    /**
     * Connexion d’un utilisateur à partir de son email et mot de passe.
     *
     * @param email    Email de l’utilisateur
     * @param password Mot de passe en clair
     * @return l’utilisateur correspondant
     * @throws UserNotFoundException   si l’utilisateur n’existe pas
     * @throws BadCredentialsException si le mot de passe est incorrect
     */
    @Override
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Mot de passe incorrect");
        }
        return user;
    }

    /**
     * Supprime un utilisateur uniquement côté auth-service.
     *
     * @param id Identifiant de l’utilisateur
     */
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        auditClient.logEvent("auth-service", "USER_DELETED", "Utilisateur supprimé avec id: " + id);
    }

    /**
     * Supprime un utilisateur avec cascade côté user-service et auth-service.
     *
     * @param userId identifiant de l’utilisateur
     * @return {@code true} si la suppression a eu lieu, {@code false} sinon
     */
    @Transactional
    public boolean deleteUserWithCascade(Long userId) {
        String url = userServiceUrl + "/" + userId + "/cascade";
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

    /**
     * Supprime un utilisateur à partir de son ID, puis tente la suppression cascade
     * côté user-service.
     *
     * @param userId identifiant de l’utilisateur
     * @throws UserNotFoundException si l’utilisateur n’existe pas
     */
    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID : " + userId));

        userRepository.delete(user);

        String url = userServiceUrl + "/" + userId + "/cascade";
        try {
            restTemplate.delete(url);
            System.out.println("✅ Suppression cascade réussie dans user-service pour id=" + userId);
        } catch (Exception e) {
            System.err.println(
                    "⚠️ Erreur lors de la suppression dans user-service (id=" + userId + ") : " + e.getMessage());
        }

        auditClient.logEvent("auth-service", "USER_DELETED", "Utilisateur supprimé avec id: " + userId);
    }

    /**
     * Recherche un utilisateur par son pseudo.
     *
     * @param pseudo pseudo recherché
     * @return l’utilisateur correspondant
     * @throws UserNotFoundException si aucun utilisateur ne correspond
     */
    @Override
    public User getUserByPseudo(String pseudo) {
        return userRepository.findByPseudo(pseudo)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec le pseudo : " + pseudo));
    }

    /**
     * Change le mot de passe d’un utilisateur authentifié.
     *
     * @param userEmail email de l’utilisateur
     * @param request   DTO contenant les nouveaux mots de passe
     * @throws IllegalArgumentException si les mots de passe ne correspondent pas
     * @throws UserNotFoundException    si l’utilisateur n’existe pas
     */
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

    /**
     * Génère un code de réinitialisation et l’associe à un utilisateur.
     * 
     * @param pseudo pseudo de l’utilisateur
     */
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

    /**
     * Génère un code de réinitialisation aléatoire.
     *
     * @param length longueur du code
     * @return code généré
     */
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
     * Vérifie la validité d’un code de réinitialisation.
     *
     * @param pseudo pseudo de l’utilisateur
     * @param code   code de réinitialisation fourni
     * @return {@code true} si valide, sinon {@code false}
     */
    @Override
    public boolean validateResetCode(String pseudo, String code) {
        User user = getUserByPseudo(pseudo);
        String storedCode = user.getResetCode();
        Long expiration = user.getResetPasswordCodeExpiration();

        return storedCode != null && storedCode.equalsIgnoreCase(code)
                && expiration != null && expiration > System.currentTimeMillis();
    }

    /**
     * Réinitialise le mot de passe d’un utilisateur avec un code valide.
     *
     * @param pseudo      pseudo de l’utilisateur
     * @param resetCode   code de réinitialisation
     * @param newPassword nouveau mot de passe
     * @throws IllegalArgumentException si le code est invalide ou expiré
     */
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

    /**
     * Envoie un e-mail contenant un code de réinitialisation de mot de passe à un
     * utilisateur.
     *
     * @param email adresse e-mail du destinataire.
     * @param code  code unique de réinitialisation du mot de passe (valide 24h).
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
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Met à jour le profil d'un utilisateur (pseudo et/ou e-mail).
     * 
     * Si aucun changement n'est détecté, retourne une réponse indiquant
     * qu'aucune modification n'a été effectuée. Si un changement est appliqué,
     * le profil est mis à jour en base de données, synchronisé avec le
     * {@code user-service}, un nouvel éventuel JWT est généré et un événement
     * d'audit est loggé.
     *
     * @param userId    identifiant unique de l'utilisateur à mettre à jour.
     * @param newPseudo nouveau pseudo (ou {@code null} / vide si inchangé).
     * @param newEmail  nouvelle adresse e-mail (ou {@code null} / vide si
     *                  inchangé).
     * @return une {@link ReponseProfileChange} contenant le résultat de la mise à
     *         jour
     *         et un nouveau jeton JWT si nécessaire.
     * @throws UserNotFoundException si l'utilisateur n'existe pas.
     */
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
            headers.set("X-User-Name", user.getPseudo());

            UserProfileDTO body = new UserProfileDTO(user.getId(), user.getPseudo(), user.getEmail());
            HttpEntity<UserProfileDTO> request = new HttpEntity<>(body, headers);

            String updateUrl = userServiceUrl + "/" + user.getId();
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

    /**
     * Change le mot de passe d'un utilisateur identifié par son pseudo.
     * 
     * Vérifie que :
     * <ul>
     * <li>le nouveau mot de passe et sa confirmation correspondent</li>
     * <li>le nouveau mot de passe respecte les règles de sécurité via
     * {@link PasswordValidator}</li>
     * <li>le mot de passe actuel fourni est correct</li>
     * </ul>
     * Si toutes les conditions sont respectées, le mot de passe est encodé et mis à
     * jour.
     *
     * @param userPseudo pseudo de l'utilisateur dont le mot de passe doit être
     *                   changé.
     * @param request    objet contenant l'ancien, le nouveau et la confirmation du
     *                   mot de passe.
     * @throws UserNotFoundException    si l'utilisateur n'existe pas.
     * @throws BadCredentialsException  si l'ancien mot de passe fourni est
     *                                  incorrect.
     * @throws IllegalArgumentException si les deux nouveaux mots de passe ne
     *                                  correspondent pas.
     */
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

        auditClient.logEvent(
                "auth-service",
                "PASSWORD_CHANGED",
                "Mot de passe modifié manuellement pour id: " + user.getId() + " | pseudo: " + user.getPseudo());
    }

    /**
     * Crée un profil utilisateur dans le microservice {@code user-service}.
     * 
     * Le profil est créé avec les informations fournies (id, pseudo, e-mail).
     * L'appel se fait via {@link RestTemplate} avec un jeton JWT généré
     * pour garantir l'authentification.
     *
     * @param id     identifiant unique de l'utilisateur.
     * @param pseudo pseudo de l'utilisateur.
     * @param email  adresse e-mail de l'utilisateur.
     */
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

    /**
     * Supprime tous les jetons de réinitialisation de mot de passe associés à un
     * utilisateur.
     *
     * @param userId identifiant unique de l'utilisateur.
     */
    public void deletePasswordResetTokens(Long userId) {
        passwordResetTokenRepository.deleteByUserId(userId);
    }

    /**
     * Recherche un utilisateur par son identifiant unique.
     *
     * @param id identifiant unique de l'utilisateur.
     * @return l'entité {@link User} correspondante.
     * @throws UserNotFoundException si aucun utilisateur n'est trouvé.
     */
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur avec id " + id + " non trouvé"));
    }

    /**
     * Convertit une entité {@link User} en un objet {@link UserResponseDTO}.
     * 
     * L'objet retourné contient les informations principales de l'utilisateur
     * (id, pseudo, e-mail) ainsi que l'ensemble de ses rôles.
     *
     * @param user entité utilisateur à convertir.
     * @return un DTO {@link UserResponseDTO} contenant les données utilisateur.
     */
    @Override
    public UserResponseDTO toDTO(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());
        return new UserResponseDTO(user.getId(), user.getPseudo(), user.getEmail(), roleNames);
    }

}
