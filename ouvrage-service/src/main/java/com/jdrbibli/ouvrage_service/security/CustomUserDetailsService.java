package com.jdrbibli.ouvrage_service.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service de gestion des utilisateurs pour Spring Security.
 * <p>
 * Implémente {@link UserDetailsService} pour fournir les informations d'authentification
 * nécessaires à Spring Security lors du login.
 * <p>
 * Cette implémentation est un exemple simple avec un utilisateur "admin" codé en dur.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * Charge les détails d'un utilisateur par son nom d'utilisateur.
     *
     * @param username le nom d'utilisateur
     * @return les détails de l'utilisateur sous forme de {@link UserDetails}
     * @throws UsernameNotFoundException si l'utilisateur n'est pas trouvé
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("admin".equals(username)) {
            return User.withUsername("admin")
                    .password("{noop}password")
                    .roles("USER")
                    .build();
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}
