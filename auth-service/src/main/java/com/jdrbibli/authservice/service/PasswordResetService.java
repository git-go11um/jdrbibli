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
     * Crée un token de réinitialisation de mot de passe pour l'utilisateur et l'envoie par email.
     * @param email L'email de l'utilisateur ayant demandé la réinitialisation
     */
    public void createPasswordResetToken(String email) {
        // Chercher l'utilisateur correspondant à l'email
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            // Pour des raisons de sécurité, tu pourrais ne rien dire et faire semblant de l'envoyer
            throw new RuntimeException("Utilisateur non trouvé avec cet email.");
        }

        User user = userOpt.get();

        // Générer un token unique et une date d'expiration
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(24);

        // Créer et sauvegarder le token
        PasswordResetToken resetToken = new PasswordResetToken(token, expiryDate, user);
        tokenRepository.save(resetToken);

        // Envoyer l'email de réinitialisation
        emailService.sendPasswordResetEmail(email, token);
    }

    /**
     * Réinitialise le mot de passe de l'utilisateur si le token est valide et non expiré.
     * @param token Le token fourni par l'utilisateur
     * @param newPassword Le nouveau mot de passe en clair
     */
    public void resetPassword(String token, String newPassword) {
        // Chercher le token en base
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalide."));

        // Vérifier qu'il n'est pas expiré
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Le token est expiré.");
        }

        // Récupérer l'utilisateur lié au token
        User user = resetToken.getUser();

        // Encoder le nouveau mot de passe et le sauvegarder
        user.setMotDePasse(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Supprimer le token après usage pour éviter toute réutilisation
        tokenRepository.delete(resetToken);
    }
}
