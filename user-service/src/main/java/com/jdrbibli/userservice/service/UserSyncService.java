package com.jdrbibli.userservice.service;

import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class UserSyncService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    /**
     * Synchronise tous les utilisateurs depuis l'auth-service
     */
    public void syncAllUsersFromAuthService() {
        try {
            // Récupérer tous les utilisateurs depuis l'auth-service
            List<Map<String, Object>> authUsers = webClientBuilder.baseUrl("http://localhost:8081")
                    .build()
                    .get()
                    .uri("/auth/users")
                    .retrieve()
                    .bodyToFlux(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {
                    })
                    .collectList()
                    .block();

            if (authUsers != null) {
                for (Map<String, Object> authUser : authUsers) {
                    String pseudo = (String) authUser.get("pseudo");
                    String email = (String) authUser.get("email");

                    // Vérifier si l'utilisateur existe déjà dans user-service
                    if (!userProfileRepository.findByPseudo(pseudo).isPresent()) {
                        UserProfile userProfile = new UserProfile();
                        userProfile.setPseudo(pseudo);
                        userProfile.setEmail(email);
                        userProfile.setAvatarUrl(null);
                        userProfileRepository.save(userProfile);
                        System.out.println("Utilisateur synchronisé: " + pseudo);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la synchronisation: " + e.getMessage());
        }
    }
}