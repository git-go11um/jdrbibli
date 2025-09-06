package com.jdrbibli.userservice.service;

import com.jdrbibli.userservice.config.StorageProperties;
import com.jdrbibli.userservice.dto.FriendDTO;
import com.jdrbibli.userservice.dto.OuvrageDTO;
import com.jdrbibli.userservice.dto.UserProfileDTO;
import com.jdrbibli.userservice.entity.User;
import com.jdrbibli.userservice.mapper.FriendMapper;
import com.jdrbibli.userservice.repository.UserRepository;
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

    private final UserRepository userRepository;
    private final WebClient webClient;
    private final StorageProperties storageProperties;
    private final FriendRequestService friendRequestService;

    @Autowired
    public UserProfileService(UserRepository userRepository,
            FriendRequestService friendRequestService,
            WebClient webClient,
            StorageProperties storageProperties) {
        this.userRepository = userRepository;
        this.friendRequestService = friendRequestService;
        this.webClient = webClient;
        this.storageProperties = storageProperties;
    }

    /** Récupère tous les utilisateurs */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /** Recherche un utilisateur par ID */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /** Recherche un utilisateur par pseudo */
    public Optional<User> findByPseudo(String pseudo) {
        return userRepository.findByPseudo(pseudo);
    }

    /** Crée un nouvel utilisateur */
    // UserProfileService
    public UserProfileDTO createUser(UserProfileDTO dto) {
        User user = new User();
        user.setId(dto.getId()); // <- ID provenant d'auth-service
        user.setPseudo(dto.getPseudo());
        user.setEmail(dto.getEmail());
        User saved = userRepository.save(user);
        return new UserProfileDTO(saved.getId(), saved.getPseudo(), saved.getEmail());
    }
    

    /** Supprime un utilisateur par pseudo */
    public void deleteUserByPseudo(String pseudo) {
        Optional<User> user = userRepository.findByPseudo(pseudo);
        if (user.isPresent()) {
            userRepository.delete(user.get());
        } else {
            throw new RuntimeException("Utilisateur non trouvé avec le pseudo: " + pseudo);
        }
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

    /** Récupère la liste des amis d’un utilisateur sous forme de FriendDTO */
    public List<FriendDTO> getFriends(String pseudo) {
        User user = userRepository.findByPseudo(pseudo)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + pseudo));

        List<User> friends = friendRequestService.listFriends(user.getId());
        return friends.stream()
                .map(FriendMapper::toDTO)
                .collect(Collectors.toList());
    }

    /** Sauvegarde l’avatar d’un utilisateur */
    public boolean saveUserAvatar(String pseudo, MultipartFile file) throws IOException {
        User user = userRepository.findByPseudo(pseudo)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        boolean wasEmpty = (user.getAvatarPath() == null || user.getAvatarPath().isBlank());

        Path uploadPath = Paths.get(storageProperties.getUploadDir());
        Files.createDirectories(uploadPath);

        String extension = Optional.ofNullable(file.getOriginalFilename())
                .filter(n -> n.contains("."))
                .map(n -> n.substring(n.lastIndexOf('.')))
                .orElse(".bin");

        String fileName = "user_" + user.getId() + extension;
        Path filePath = uploadPath.resolve(fileName);

        Files.write(filePath, file.getBytes());

        user.setAvatarPath(filePath.toString());
        user.setAvatarUrl("/api/users/profile/avatar/" + user.getId());
        userRepository.save(user);

        return wasEmpty;
    }

    /** Récupère l’avatar d’un utilisateur */
    public byte[] getUserAvatar(Long id) throws IOException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        String path = user.getAvatarPath();
        if (path == null || path.isBlank()) {
            throw new FileNotFoundException("Aucun avatar disponible pour cet utilisateur.");
        }

        return Files.readAllBytes(Paths.get(path));
    }

    /** Récupère un utilisateur par ID */
    public User getUserProfileById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}
