package com.jdrbibli.authservice.service;

import com.jdrbibli.authservice.entity.PasswordResetToken;
import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.repository.PasswordResetTokenRepository;
import com.jdrbibli.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Service gérant la réinitialisation de mot de passe.
 * Crée des tokens, les envoie par mail et valide les demandes de reset.
 */
@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    /**
     * Crée un token de réinitialisation et l'envoie par email.
     *
     * @param pseudo le pseudo de l'utilisateur
     */
    public void createPasswordResetToken(String pseudo) {
        // Récupération de l'utilisateur
        Optional<User> userOpt = userRepository.findByPseudo(pseudo);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Utilisateur non trouvé avec ce pseudo.");
        }
        User user = userOpt.get();

        // Création du token et date d'expiration
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(24);

        // Enregistrement du token en base
        PasswordResetToken resetToken = new PasswordResetToken(token, expiryDate, user);
        tokenRepository.save(resetToken);

        // Envoi du mail avec le token
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    /**
     * Réinitialise le mot de passe en validant le token.
     *
     * @param token       le token reçu par email
     * @param newPassword le nouveau mot de passe
     */
    public void resetPassword(String token, String newPassword) {
        // Recherche du token en base
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalide."));

        // Vérification de l'expiration
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Le token est expiré.");
        }

        // Mise à jour du mot de passe de l'utilisateur
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Suppression du token après usage
        tokenRepository.delete(resetToken);
    }
}
