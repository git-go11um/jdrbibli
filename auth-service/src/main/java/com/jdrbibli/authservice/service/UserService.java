package com.jdrbibli.authservice.service;

import com.jdrbibli.authservice.dto.ChangePasswordRequest;
import com.jdrbibli.authservice.dto.UserResponseDTO;
import com.jdrbibli.authservice.entity.Role;
import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.exception.BadCredentialsException;
import com.jdrbibli.authservice.exception.UserNotFoundException;
import com.jdrbibli.authservice.repository.UserRepository;
import com.jdrbibli.authservice.util.PasswordValidator;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    @Override
    public User inscrireNewUser(String pseudo, String email, String motDePasse) {
        PasswordValidator.validate(motDePasse);
        String hashedPassword = passwordEncoder.encode(motDePasse);
        User newUser = new User(null, pseudo, email, hashedPassword, new HashSet<>(), null, null);
        return userRepository.save(newUser);
    }

    @Override
    public User login(String email, String motDePasse) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé"));
        if (!passwordEncoder.matches(motDePasse, user.getPassword())) {
            throw new BadCredentialsException("Mot de passe incorrect");
        }
        return user;
    }

    @Override
    public void supprimerUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Utilisateur avec id " + userId + " non trouvé");
        }
        userRepository.deleteById(userId);
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

        // Générer un code court
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        // Expiration dans 24h
        long expirationTime = System.currentTimeMillis() + 24 * 60 * 60 * 1000;

        user.setResetCode(code);
        user.setResetPasswordCodeExpiration(expirationTime);

        userRepository.save(user);

        sendResetPasswordEmail(user.getEmail(), code);
    }

    /**
     * Vérifie si le code fourni correspond et n'a pas expiré
     */
    @Override
    public boolean validateResetCode(String pseudo, String code) {
        User user = getUserByPseudo(pseudo);
        String storedCode = user.getResetCode();
        Long expiration = user.getResetPasswordCodeExpiration();

        return storedCode != null && storedCode.equals(code) && expiration != null && expiration > System.currentTimeMillis();
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

    
}
