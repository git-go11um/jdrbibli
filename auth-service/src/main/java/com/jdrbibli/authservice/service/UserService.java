package com.jdrbibli.authservice.service;

import com.jdrbibli.authservice.dto.UserResponseDTO;
import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.repository.UserRepository;
import com.jdrbibli.authservice.exception.UserNotFoundException;
import com.jdrbibli.authservice.exception.BadCredentialsException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User inscrireNewUser(String pseudo, String email, String motDePasse) {
        String hashedPassword = passwordEncoder.encode(motDePasse);
        User newUser = new User(null, pseudo, email, hashedPassword, new HashSet<>());
        return userRepository.save(newUser);
    }

    @Override
    public User login(String email, String motDePasse) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé"));

        if (!passwordEncoder.matches(motDePasse, user.getMotDePasse())) {
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
                .map(role -> role.getName())
                .collect(Collectors.toSet());
        return new UserResponseDTO(user.getId(), user.getPseudo(), user.getEmail(), roleNames);
    }

    @Override
    public User getUserByPseudo(String pseudo) {
        return userRepository.findByPseudo(pseudo)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec le pseudo : " + pseudo));
    }

}
