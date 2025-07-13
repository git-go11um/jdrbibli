package com.jdrbibli.userservice.service;

import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.repository.UserProfileRepository;
import com.jdrbibli.userservice.dto.OuvrageDTO; // Import du DTO Ouvrage
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final WebClient.Builder webClientBuilder;
    private final String uploadDir = "uploads/avatars/";

    @Autowired
    public UserProfileService(UserProfileRepository userProfileRepository, WebClient.Builder webClientBuilder) {
        this.userProfileRepository = userProfileRepository;
        this.webClientBuilder = webClientBuilder;

        // Créer le répertoire d'upload s'il n'existe pas
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Impossible de créer le répertoire d'upload", e);
        }
    }

    public List<UserProfile> getAllUsers() {
        return userProfileRepository.findAll();
    }

    public Optional<UserProfile> getUserById(Long id) {
        return userProfileRepository.findById(id);
    }

    public UserProfile createUser(UserProfile userProfile) {
        // Par exemple, récupérer les ouvrages avant de créer l'utilisateur
        /* List<OuvrageDTO> ouvrages = getOuvragesForUser(userProfile.getId()); WYL */
        /* userProfile.setLudotheque(ouvrages); WYL */// Associer les ouvrages au profil utilisateur

        return userProfileRepository.save(userProfile);
    }

    public void deleteUser(Long id) {
        userProfileRepository.deleteById(id);
    }

    // Méthode modifiée pour uploader l'avatar
    public String uploadAvatar(String pseudo, MultipartFile file) throws IOException {
        // Vérifier que le fichier n'est pas vide
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }

        // Vérifier le type de fichier (images seulement)
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Seules les images sont autorisées");
        }

        // Générer un nom de fichier unique
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;

        // Sauvegarder le fichier
        Path filePath = Paths.get(uploadDir + filename);
        Files.copy(file.getInputStream(), filePath);

        // Construire l'URL de l'avatar
        String avatarUrl = "/api/users/avatars/" + filename;

        // Vérifier si l'utilisateur existe, sinon le créer
        UserProfile userProfile = userProfileRepository.findByPseudo(pseudo)
                .orElseThrow(() -> new RuntimeException(
                        "Utilisateur non trouvé: " + pseudo + ". Merci de vous reconnecter."));

        // Mettre à jour l'URL de l'avatar
        userProfile.setAvatarUrl(avatarUrl);
        userProfileRepository.save(userProfile);

        return avatarUrl;
    }

    // Nouvelle méthode pour récupérer l'URL de l'avatar
    public String getAvatarUrl(String pseudo) {
        UserProfile userProfile = userProfileRepository.findByPseudo(pseudo)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + pseudo));
        return userProfile.getAvatarUrl();
    }

    // Méthode pour récupérer les ouvrages via WebClient
    public List<OuvrageDTO> getOuvragesForUser(Long userId) {
        return webClientBuilder.baseUrl("http://localhost:8084")
                .build()
                .get()
                .uri("/api/ouvrages?userId=" + userId)
                .retrieve()
                .bodyToFlux(OuvrageDTO.class)
                .collectList()
                .block();
    }

    public Optional<UserProfile> getUserByPseudo(String pseudo) {
        return userProfileRepository.findByPseudo(pseudo);
    }
}
