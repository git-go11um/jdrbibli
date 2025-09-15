package com.jdrbibli.userservice.service;

import com.jdrbibli.userservice.config.StorageProperties;
import com.jdrbibli.userservice.dto.FriendDTO;
import com.jdrbibli.userservice.dto.OuvrageDTO;
import com.jdrbibli.userservice.dto.UserProfileDTO;
import com.jdrbibli.userservice.entity.User;
import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.mapper.FriendMapper;
import com.jdrbibli.userservice.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserProfileService {

    private final WebClient webClient;
    private final StorageProperties storageProperties;
    private final FriendRequestService friendRequestService;
    private final UserProfileRepository userProfileRepository;

    @Autowired
    public UserProfileService(UserProfileRepository userProfileRepository,
            FriendRequestService friendRequestService,
            WebClient webClient,
            StorageProperties storageProperties) {
        this.userProfileRepository = userProfileRepository;
        this.friendRequestService = friendRequestService;
        this.webClient = webClient;
        this.storageProperties = storageProperties;
    }

    /** Récupère tous les profils */
    public List<UserProfile> getAllUsers() {
        return userProfileRepository.findAll();
    }

    /** Recherche un profil par ID */
    public Optional<UserProfile> getUserById(Long id) {
        return userProfileRepository.findById(id);
    }

    /** Recherche un profil par pseudo */
    public Optional<UserProfile> findByPseudo(String pseudo) {
        return userProfileRepository.findByPseudo(pseudo);
    }

    /** Crée un nouveau profil utilisateur */
    public UserProfileDTO createUser(UserProfileDTO dto) {
        // Vérifie si un profil existe déjà avec le même ID
        if (dto.getId() != null) {
            Optional<UserProfile> existingById = userProfileRepository.findById(dto.getId());
            if (existingById.isPresent()) {
                UserProfile profile = existingById.get();
                return new UserProfileDTO(profile.getId(), profile.getPseudo(), profile.getEmail());
            }
        }

        // Vérifie si un profil existe déjà avec le même pseudo
        Optional<UserProfile> existingByPseudo = userProfileRepository.findByPseudo(dto.getPseudo());
        if (existingByPseudo.isPresent()) {
            UserProfile profile = existingByPseudo.get();
            return new UserProfileDTO(profile.getId(), profile.getPseudo(), profile.getEmail());
        }

        // Sinon, crée le profil
        UserProfile profile = new UserProfile();
        profile.setId(dto.getId());
        profile.setPseudo(dto.getPseudo());
        profile.setEmail(dto.getEmail());

        UserProfile saved = userProfileRepository.save(profile);
        return new UserProfileDTO(saved.getId(), saved.getPseudo(), saved.getEmail());
    }

    /** Supprime un profil par pseudo */
    public void deleteUserByPseudo(String pseudo) {
        UserProfile profile = userProfileRepository.findByPseudo(pseudo)
                .orElseThrow(() -> new RuntimeException("Profil non trouvé avec le pseudo: " + pseudo));
        userProfileRepository.delete(profile);
    }

    /** Récupère les ouvrages d’un utilisateur via ouvrage-service */
    public List<OuvrageDTO> getOuvragesForUser(Long userId) {
        return webClient.get()
                .uri("/ouvrages?userId=" + userId)
                .retrieve()
                .bodyToFlux(OuvrageDTO.class)
                .collectList()
                .block();
    }

    /** Récupère la liste des amis d’un utilisateur */
    public List<FriendDTO> getFriends(String pseudo) {
        UserProfile profile = userProfileRepository.findByPseudo(pseudo)
                .orElseThrow(() -> new RuntimeException("Profil non trouvé: " + pseudo));

        List<User> friends = friendRequestService.listFriends(profile.getId());
        return friends.stream()
                .map(FriendMapper::toDTO)
                .collect(Collectors.toList());
    }

    /** Sauvegarde l’avatar d’un utilisateur */
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

    /** Récupère l’avatar d’un utilisateur */
    public byte[] getUserAvatar(Long id) throws IOException {
        UserProfile profile = userProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profil non trouvé"));

        String path = profile.getAvatarPath();
        if (path == null || path.isBlank()) {
            throw new FileNotFoundException("Aucun avatar disponible pour ce profil.");
        }

        return Files.readAllBytes(Paths.get(path));
    }

    /** Récupère un profil par ID */
    public UserProfile getUserProfileById(Long id) {
        return userProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profil non trouvé"));
    }

    /** Met à jour un profil */
    public Optional<UserProfileDTO> updateUser(Long id, UserProfileDTO dto) {
        return userProfileRepository.findById(id).map(profile -> {
            profile.setPseudo(dto.getPseudo());
            profile.setEmail(dto.getEmail());
            UserProfile saved = userProfileRepository.save(profile);
            return new UserProfileDTO(saved.getId(), saved.getPseudo(), saved.getEmail());
        });
    }

}
