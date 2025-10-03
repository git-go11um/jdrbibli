package com.jdrbibli.authservice.security;

import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service pour charger les informations d'un utilisateur pour Spring Security.
 * <p>
 * Implémente UserDetailsService afin que Spring Security puisse récupérer les informations
 * d'authentification (pseudo, mot de passe, rôles) d'un utilisateur.
 * </p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructeur avec injection du UserRepository.
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Méthode appelée par Spring Security pour récupérer un utilisateur par pseudo.
     *
     * @param pseudo le pseudo de l'utilisateur
     * @return UserDetails (ici l'entité User implémente UserDetails)
     * @throws UsernameNotFoundException si aucun utilisateur n'est trouvé
     */
    @Override
    public UserDetails loadUserByUsername(String pseudo) throws UsernameNotFoundException {
        // Recherche de l'utilisateur dans la base
        User user = userRepository.findByPseudo(pseudo)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur non trouvé avec pseudo: " + pseudo));

        // L'entité User implémente UserDetails, donc on peut la retourner directement
        return user;
    }
}
