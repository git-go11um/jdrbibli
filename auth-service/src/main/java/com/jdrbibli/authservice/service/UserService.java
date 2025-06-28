package com.jdrbibli.authservice.service;

import com.jdrbibli.authservice.entity.Role;
import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.repository.RoleRepository;
import com.jdrbibli.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public User inscrireNewUser(String pseudo, String email, String motDePasse) {
        // Vérifier si l'email ou le pseudo existent déjà
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email déjà utilisé");
        }
        if (userRepository.existsByPseudo(pseudo)) {
            throw new RuntimeException("Pseudo déjà utilisé");
        }

        // Hacher le mot de passe
        String motDePasseHache = passwordEncoder.encode(motDePasse);

        // Récupérer le rôle USER
        Role roleUser = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Rôle ROLE_USER introuvable dans la base de données"));

        // Créer et sauvegarder le user
        User user = User.builder()
                .pseudo(pseudo)
                .email(email)
                .motDePasse(motDePasseHache)
                .roles(Collections.singleton(roleUser))
                .build();

        return userRepository.save(user);
    }
}
