package com.jdrbibli.userservice.service;

import com.jdrbibli.userservice.dto.FriendDTO;
import com.jdrbibli.userservice.dto.OuvrageDTO;
import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.mapper.FriendMapper;
import com.jdrbibli.userservice.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public UserProfileService(UserProfileRepository userProfileRepository, WebClient.Builder webClientBuilder) {
        this.userProfileRepository = userProfileRepository;
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * Récupère tous les utilisateurs.
     */
    public List<UserProfile> getAllUsers() {
        return userProfileRepository.findAll();
    }

    /**
     * Recherche un utilisateur par ID.
     */
    public Optional<UserProfile> getUserById(Long id) {
        return userProfileRepository.findById(id);
    }

    /**
     * Recherche un utilisateur par pseudo.
     */
    public Optional<UserProfile> findByPseudo(String pseudo) {
        return userProfileRepository.findByPseudo(pseudo);
    }

    /**
     * Crée un nouvel utilisateur et lui associe sa ludothèque.
     */
    public UserProfile createUser(UserProfile userProfile) {
        List<OuvrageDTO> ouvrages = getOuvragesForUser(userProfile.getId());
        userProfile.setLudotheque(ouvrages);
        return userProfileRepository.save(userProfile);
    }

    /**
     * Supprime un utilisateur par ID.
     */
    public void deleteUser(Long id) {
        userProfileRepository.deleteById(id);
    }

    /**
     * Récupère la liste complète des ouvrages liés à un utilisateur via ouvrage-service.
     */
    public List<OuvrageDTO> getOuvragesForUser(Long userId) {
        return webClientBuilder.baseUrl("http://gateway:8084")  // URL du gateway
                .build()
                .get()
                .uri("/ouvrages?userId=" + userId)
                .retrieve()
                .bodyToFlux(OuvrageDTO.class)
                .collectList()
                .block(); // appel bloquant pour simplifier
    }

    /**
     * Récupère la liste des amis d’un utilisateur sous forme de FriendDTO.
     */
    public List<FriendDTO> getFriends(String pseudo) {
        UserProfile user = userProfileRepository.findByPseudo(pseudo)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + pseudo));
        return user.getFriends().stream()
                .map(FriendMapper::toDTO)
                .collect(Collectors.toList());
    }
}
