package com.jdrbibli.authservice.service;

import com.jdrbibli.authservice.dto.ChangePasswordRequest;
import com.jdrbibli.authservice.dto.UserResponseDTO;
import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.repository.UserRepository;
import com.jdrbibli.authservice.util.PasswordValidator;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import com.jdrbibli.authservice.exception.UserNotFoundException;
import com.jdrbibli.authservice.exception.BadCredentialsException;
import com.jdrbibli.authservice.entity.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.HashSet;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private JavaMailSender javaMailSender;

    // Injection via constructeur
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.javaMailSender = mailSender;
    }

    @Override
    public User inscrireNewUser(String pseudo, String email, String motDePasse) {
        // Vérifier la complexité du mot de passe avant de hasher
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

    public UserResponseDTO toDTO(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());
        return new UserResponseDTO(user.getId(), user.getPseudo(), user.getEmail(), roleNames);
    }

    @Override
    public User getUserByPseudo(String pseudo) {
        return userRepository.findByPseudo(pseudo)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec le pseudo : " + pseudo));
    }

    @Override
    public void changePassword(String userEmail, ChangePasswordRequest request) {
        // Vérification si les mots de passe correspondent
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("Les deux mots de passe ne correspondent pas");
        }

        // Vérification de la complexité du mot de passe
        PasswordValidator.validate(request.getNewPassword());

        // Recherche de l'utilisateur par email
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur avec l'email " + userEmail + " non trouvé"));

        // Modifier le mot de passe
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // Sauvegarder l'utilisateur avec le nouveau mot de passe
        userRepository.save(user);
    }

    // Méthode pour demander un code de réinitialisation
    @Override
    public void requestPasswordReset(String pseudo) throws MessagingException {
        // Vérifier si l'utilisateur existe
        User user = getUserByPseudo(pseudo);
        if (user == null) {
            throw new UserNotFoundException("Utilisateur non trouvé avec le pseudo : " + pseudo);
        }

        // Générer un code unique pour la réinitialisation
        String resetCode = UUID.randomUUID().toString();

        // Définir l'expiration du code (par exemple, 24 heures)
        long expirationTime = System.currentTimeMillis() + 24 * 60 * 60 * 1000; // 24h

        // Sauvegarder le code et son expiration dans la base de données
        user.setResetPasswordCode(resetCode);
        user.setResetPasswordCodeExpiration(expirationTime);

        // Sauvegarder l'utilisateur avec le nouveau code et expiration
        userRepository.save(user);

        // Envoi du mail avec le code
        sendResetPasswordEmail(user.getEmail(), resetCode);
    }

    // Méthode pour envoyer un email avec le code
    private void sendResetPasswordEmail(String email, String resetCode) {
        String subject = "Réinitialisation de votre mot de passe";
        String message = "Voici votre code de réinitialisation : " + resetCode;

        try {
            // Créer un MimeMessage
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            // Créer un objet MimeMessageHelper pour l'email
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");

            // Définir l'expéditeur et destinataire
            helper.setFrom("no-reply@jdrbibli.com");
            helper.setTo(email);
            helper.setSubject(subject);

            // Forcer l'encodage du contenu en texte brut (et éviter quoted-printable)
            mimeMessage.setText(message, "utf-8", "plain"); // 'plain' évite tout encodage HTML

            // Forcer l'absence d'encodage 'quoted-printable'
            mimeMessage.setHeader("Content-Transfer-Encoding", "8bit");

            // Envoi de l'email
            javaMailSender.send(mimeMessage);

            System.out.println("Email envoyé à : " + email);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }

    // Méthode pour valider le code de réinitialisation
    @Override
    public boolean validateResetCode(String pseudo, String code) {
        User user = getUserByPseudo(pseudo);
        String storedCode = user.getResetPasswordCode();
        long expirationTime = user.getResetPasswordCodeExpiration();

        if (storedCode != null && storedCode.equals(code) && expirationTime > System.currentTimeMillis()) {
            return true; // Code valide
        }
        return false; // Code invalide ou expiré
    }

    // Méthode pour réinitialiser le mot de passe
    @Override
    public void resetPassword(String pseudo, String resetCode, String newPassword) {
        // Vérifier si l'utilisateur existe
        User user = getUserByPseudo(pseudo);

        // Vérifier si le code est valide
        if (!validateResetCode(pseudo, resetCode)) {
            throw new IllegalArgumentException("Le code de réinitialisation est invalide ou a expiré.");
        }

        // Vérifier la complexité du mot de passe
        PasswordValidator.validate(newPassword);

        // Enregistrer le nouveau mot de passe
        user.setPassword(passwordEncoder.encode(newPassword));

        // Sauvegarder l'utilisateur avec le nouveau mot de passe
        userRepository.save(user);
    }
}
