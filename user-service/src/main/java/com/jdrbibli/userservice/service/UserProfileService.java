package com.jdrbibli.userservice.service;

import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.repository.UserProfileRepository;
import com.jdrbibli.userservice.dto.OuvrageDTO;  // Import du DTO Ouvrage
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Optional;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public UserProfileService(UserProfileRepository userProfileRepository, WebClient.Builder webClientBuilder) {
        this.userProfileRepository = userProfileRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public List<UserProfile> getAllUsers() {
        return userProfileRepository.findAll();
    }

    public Optional<UserProfile> getUserById(Long id) {
        return userProfileRepository.findById(id);
    }

    public UserProfile createUser(UserProfile userProfile) {
        // Par exemple, récupérer les ouvrages avant de créer l'utilisateur
        List<OuvrageDTO> ouvrages = getOuvragesForUser(userProfile.getId());
        userProfile.setLudotheque(ouvrages);  // Associer les ouvrages au profil utilisateur
        
        return userProfileRepository.save(userProfile);
    }

    public void deleteUser(Long id) {
        userProfileRepository.deleteById(id);
    }

    // Méthode pour récupérer les ouvrages via WebClient
    public List<OuvrageDTO> getOuvragesForUser(Long userId) {
        return webClientBuilder.baseUrl("http://gateway:8080")  // L'URL de ton gateway
            .build()
            .get()
            .uri("/ouvrages?userId=" + userId)  // L'URL vers l'API d'ouvrage-service via gateway
            .retrieve()
            .bodyToFlux(OuvrageDTO.class)  // Conversion en OuvrageDTO
            .collectList()
            .block();  // Pour un appel synchrone (bloque jusqu'à la réponse)
    }
}
