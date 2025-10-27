package com.jdrbibli.userservice.service;

import com.jdrbibli.userservice.config.StorageProperties;
import com.jdrbibli.userservice.dto.FriendDTO;
import com.jdrbibli.userservice.dto.OuvrageDTO;
import com.jdrbibli.userservice.dto.UserProfileDTO;
import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.mapper.FriendMapper;
import com.jdrbibli.userservice.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des profils utilisateurs.
 * 
 * Permet la création, la mise à jour et la suppression des profils,
 * la gestion des avatars, et la récupération des amis et des ouvrages.
 * 
 */
@Service
public class UserProfileService {

    private final WebClient webClient;
    private final WebClient authWebClient;
    private final StorageProperties storageProperties;
    private final FriendRequestService friendRequestService;
    private final UserProfileRepository userProfileRepository;
    private static final Logger log = LoggerFactory.getLogger(UserProfileService.class);

    @Autowired
    public UserProfileService(
            UserProfileRepository userProfileRepository,
            FriendRequestService friendRequestService,
            WebClient webClient,
            @Qualifier("authWebClient") WebClient authWebClient,
            StorageProperties storageProperties) {

        this.userProfileRepository = userProfileRepository;
        this.friendRequestService = friendRequestService;
        this.webClient = webClient;
        this.authWebClient = authWebClient;
        this.storageProperties = storageProperties;
    }

    /**
     * Récupère tous les utilisateurs.
     *
     * @return liste de tous les UserProfile
     */
    public List<UserProfile> getAllUsers() {
        return userProfileRepository.findAll();
    }

    /**
     * Récupère un utilisateur par son identifiant.
     *
     * @param id ID de l'utilisateur
     * @return Optional contenant l'utilisateur si trouvé
     */
    public Optional<UserProfile> getUserById(Long id) {
        return userProfileRepository.findById(id);
    }

    /**
     * Recherche un utilisateur par pseudo.
     *
     * @param pseudo pseudo recherché
     * @return Optional contenant l'utilisateur si trouvé
     */
    public Optional<UserProfile> findByPseudo(String pseudo) {
        return userProfileRepository.findByPseudo(pseudo);
    }

    /**
     * Crée un nouvel utilisateur si le pseudo n'existe pas.
     *
     * @param dto DTO contenant les informations de l'utilisateur
     * @return DTO de l'utilisateur créé ou existant
     */
    public UserProfileDTO createUser(UserProfileDTO dto) {
        if (dto.getId() != null) {
            Optional<UserProfile> existingById = userProfileRepository.findById(dto.getId());
            if (existingById.isPresent()) {
                UserProfile profile = existingById.get();
                return new UserProfileDTO(profile.getId(), profile.getPseudo(), profile.getEmail());
            }
        }

        Optional<UserProfile> existingByPseudo = userProfileRepository.findByPseudo(dto.getPseudo());
        if (existingByPseudo.isPresent()) {
            UserProfile profile = existingByPseudo.get();
            return new UserProfileDTO(profile.getId(), profile.getPseudo(), profile.getEmail());
        }

        UserProfile profile = new UserProfile();
        profile.setId(dto.getId());
        profile.setPseudo(dto.getPseudo());
        profile.setEmail(dto.getEmail());

        UserProfile saved = userProfileRepository.save(profile);
        return new UserProfileDTO(saved.getId(), saved.getPseudo(), saved.getEmail());
    }

    /**
     * Supprime un utilisateur par pseudo.
     *
     * @param pseudo pseudo de l'utilisateur
     * @return message de confirmation
     */
    public String deleteUserByPseudo(String pseudo) {
        userProfileRepository.findByPseudo(pseudo)
                .ifPresent(profile -> userProfileRepository.delete(profile));
        return "Utilisateur supprimé avec succès";
    }

    /**
     * Récupère les ouvrages associés à un utilisateur.
     *
     * @param userId ID de l'utilisateur
     * @return liste des OuvrageDTO
     */
    public List<OuvrageDTO> getOuvragesForUser(Long userId) {
        return webClient.get()
                .uri("/ouvrages?userId=" + userId)
                .retrieve()
                .bodyToFlux(OuvrageDTO.class)
                .collectList()
                .block();
    }

    /**
     * Récupère les amis d'un utilisateur.
     *
     * @param pseudo pseudo de l'utilisateur
     * @return liste de FriendDTO
     */
    public List<FriendDTO> getFriends(String pseudo) {
        UserProfile profile = userProfileRepository.findByPseudo(pseudo)
                .orElseThrow(() -> new RuntimeException("Profil non trouvé: " + pseudo));

        List<UserProfile> friends = friendRequestService.listFriends(profile.getId());
        return friends.stream()
                .map(FriendMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Sauvegarde l'avatar d'un utilisateur.
     *
     * @param pseudo pseudo de l'utilisateur
     * @param file   fichier avatar
     * @return true si l'utilisateur n'avait pas d'avatar précédemment
     * @throws IOException si l'écriture du fichier échoue
     */
    public boolean saveUserAvatar(String pseudo, MultipartFile file) throws IOException {
        UserProfile profile = userProfileRepository.findByPseudo(pseudo)
                .orElseThrow(() -> new RuntimeException("Profil utilisateur introuvable"));

        boolean wasEmpty = (profile.getAvatarPath() == null || profile.getAvatarPath().isBlank());

        Path uploadPath = Paths.get(storageProperties.getUploadDir());
        Files.createDirectories(uploadPath);

        String extension = Optional.ofNullable(file.getOriginalFilename())
                .filter(n -> n.contains("."))
                .map(n -> n.substring(n.lastIndexOf('.')))
                .orElse(".bin");

        String fileName = "user_" + profile.getId() + extension;
        Path filePath = uploadPath.resolve(fileName);

        Files.write(filePath, file.getBytes());

        profile.setAvatarPath(filePath.toString());
        profile.setAvatarUrl("/api/users/profile/avatar/" + profile.getId());
        userProfileRepository.save(profile);

        return wasEmpty;
    }

    /**
     * Récupère l'avatar d'un utilisateur.
     *
     * @param id ID de l'utilisateur
     * @return tableau d'octets représentant l'image
     * @throws IOException si la lecture du fichier échoue
     */
    public byte[] getUserAvatar(Long id) throws IOException {
        UserProfile profile = userProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profil non trouvé"));

        String path = profile.getAvatarPath();
        if (path == null || path.isBlank()) {
            throw new FileNotFoundException("Aucun avatar disponible pour ce profil.");
        }

        return Files.readAllBytes(Paths.get(path));
    }

    /**
     * Récupère le profil utilisateur complet par ID.
     *
     * @param id ID de l'utilisateur
     * @return UserProfile
     */
    public UserProfile getUserProfileById(Long id) {
        return userProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profil non trouvé"));
    }

    /**
     * Met à jour un utilisateur.
     *
     * @param id  ID de l'utilisateur
     * @param dto DTO avec les nouvelles informations
     * @return Optional contenant le DTO mis à jour
     */
    public Optional<UserProfileDTO> updateUser(Long id, UserProfileDTO dto) {
        return userProfileRepository.findById(id).map(profile -> {
            profile.setPseudo(dto.getPseudo());
            profile.setEmail(dto.getEmail());
            UserProfile saved = userProfileRepository.save(profile);
            return new UserProfileDTO(saved.getId(), saved.getPseudo(), saved.getEmail());
        });
    }

    /**
     * Supprime un utilisateur par ID.
     *
     * @param id ID de l'utilisateur
     * @return true si la suppression a réussi
     */
    public boolean deleteUserById(Long id) {
        if (userProfileRepository.existsById(id)) {
            userProfileRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Supprime un utilisateur et ses gammes associées (cascade).
     *
     * @param id ID de l'utilisateur
     * @return true si la suppression a réussi
     */
    public boolean deleteUserByIdWithCascade(Long id) {
        Optional<UserProfile> optionalProfile = userProfileRepository.findById(id);
        if (optionalProfile.isEmpty()) {
            log.warn("Tentative de suppression d'un profil utilisateur inexistant : {}", id);
            return false;
        }

        UserProfile profile = optionalProfile.get();

        try {
            // 🗑️ 1. Suppression locale (user_db)
            userProfileRepository.delete(profile);
            log.info("✅ Profil utilisateur {} supprimé de user_db.", id);

            // 🌐 2. Suppression dans auth-service via WebClient Docker
            log.info("➡️ Appel auth-service pour supprimer user {}", id);
            authWebClient.delete()
                    .uri("/users/" + id + "/cascade")
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.info("✅ Utilisateur {} supprimé dans auth-service.", id);
            return true;

        } catch (Exception e) {
            log.error("❌ Erreur lors de la suppression de l'utilisateur {}", id, e);
            return false;
        }
    }

    /**
     * Vérifie si deux utilisateurs sont amis.
     *
     * @param userId   ID du premier utilisateur
     * @param friendId ID du second utilisateur
     * @return true si les utilisateurs sont amis
     */
    public boolean areFriends(Long userId, Long friendId) {
        List<UserProfile> friends = friendRequestService.listFriends(userId);
        return friends.stream().anyMatch(f -> f.getId().equals(friendId));
    }

}
