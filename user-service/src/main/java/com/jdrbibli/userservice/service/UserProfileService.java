package com.jdrbibli.userservice.service;

import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.repository.UserProfileRepository;

import reactor.core.publisher.Mono;

import com.jdrbibli.userservice.dto.OuvrageDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final WebClient.Builder webClientBuilder;

    // Répertoire de stockage des avatars (configurable dans application.properties
    // ou yml)
    @Value("${user.profile.avatar.upload-dir}")
    private String uploadDir;

    public UserProfileService(UserProfileRepository userProfileRepository, WebClient.Builder webClientBuilder) {
        this.userProfileRepository = userProfileRepository;
        this.webClientBuilder = webClientBuilder;
    }

    // Récupérer tous les utilisateurs
    public List<UserProfile> getAllUsers() {
        return userProfileRepository.findAll();
    }

    // Récupérer un utilisateur par son ID
    public Optional<UserProfile> getUserById(Long id) {
        return userProfileRepository.findById(id);
    }

    // Créer un nouvel utilisateur et lui associer la ludothèque
    public UserProfile createUser(UserProfile userProfile) {
        // IMPORTANT : userProfile.getId() sera null avant sauvegarde, donc on ne peut
        // pas l'utiliser ici.
        // Si tu veux récupérer la ludothèque au moment de la création, il faut revoir
        // la logique.
        // Sinon, tu peux enregistrer d'abord, puis récupérer et setter la ludothèque
        // ensuite.

        // Exemple d'approche correcte :
        UserProfile savedUser = userProfileRepository.save(userProfile);

        List<OuvrageDTO> ouvrages = getOuvragesForUser(savedUser.getId());
        savedUser.setLudotheque(ouvrages);

        return userProfileRepository.save(savedUser);
    }

    // Supprimer un utilisateur
    public void deleteUser(Long id) {
        userProfileRepository.deleteById(id);
    }

    // Récupérer les ouvrages liés à l'utilisateur via WebClient (passage par
    // gateway)
    public List<OuvrageDTO> getOuvragesForUser(Long userId) {
        try {
            return webClientBuilder.baseUrl("http://localhost:8084")
                    .build()
                    .get()
                    .uri("/ouvrages?userId=" + userId)
                    .retrieve()
                    .bodyToFlux(OuvrageDTO.class)
                    .collectList()
                    .block();
        } catch (WebClientResponseException e) {
            // Log de l'exception pour une meilleure visibilité
            System.err.println("Error calling Ouvrage service: " + e.getMessage());
            throw new RuntimeException("Error fetching ouvrages: " + e.getMessage(), e);
        }
    }

    // Stocker l'image d'avatar pour un utilisateur
    public String storeUserAvatar(String username, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        Path filePath = Path.of(uploadDir, fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        UserProfile userProfile = userProfileRepository.findByPseudo(username)
                .orElseThrow(() -> new IllegalStateException("Utilisateur non trouvé pour " + username));

        String imageUrl = "/uploads/avatars/" + fileName;
        userProfile.setAvatarUrl(imageUrl);
        userProfileRepository.save(userProfile);

        return imageUrl;
    }
}
