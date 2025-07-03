package com.jdrbibli.authservice.service;

import com.jdrbibli.authservice.dto.ChangePasswordRequest;
import com.jdrbibli.authservice.dto.UserResponseDTO;
import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.repository.UserRepository;
import com.jdrbibli.authservice.util.PasswordValidator;
import com.jdrbibli.authservice.exception.UserNotFoundException;
import com.jdrbibli.authservice.exception.BadCredentialsException;
import com.jdrbibli.authservice.entity.Role;

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
        // Vérifier la complexité du mot de passe avant de hasher
        PasswordValidator.validate(motDePasse);

        String hashedPassword = passwordEncoder.encode(motDePasse);
        User newUser = new User(null, pseudo, email, hashedPassword, new HashSet<>());
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
    public void changePassword(String userPseudo, ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("Les deux mots de passe ne correspondent pas");
        }

        // Vérifier la complexité du nouveau mot de passe
        PasswordValidator.validate(request.getNewPassword());

        User user = userRepository.findByPseudo(userPseudo)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
