package com.jdrbibli.authservice.service;

import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class UserService implements IUserService{

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User inscrireNewUser(String pseudo, String email, String motDePasse) {
        // Crée un nouvel utilisateur avec constructeur complet (rôle vide par défaut)
        User newUser = new User(null, pseudo, email, motDePasse, new HashSet<>());

        // Sauvegarde en base
        return userRepository.save(newUser);
    }
}
