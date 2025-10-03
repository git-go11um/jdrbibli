package com.jdrbibli.userservice.service;

import com.jdrbibli.userservice.dto.OuvrageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Service pour la gestion de la ludothèque d'un utilisateur.
 * <p>
 * Permet de récupérer les ouvrages d'un utilisateur via le service Ouvrage
 * en utilisant WebClient.
 * </p>
 */
@Service
public class UserLudothequeService {

    private final WebClient webClient;

    @Autowired
    public UserLudothequeService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Récupère une liste d'ouvrages à partir de leurs identifiants.
     *
     * @param ouvrageIds Liste des IDs des ouvrages
     * @return liste des OuvrageDTO correspondants, ou une liste vide si aucun ID fourni
     */
    public List<OuvrageDTO> getOuvragesByIds(List<Long> ouvrageIds) {
        if (ouvrageIds == null || ouvrageIds.isEmpty()) {
            return List.of();
        }

        String idsParam = String.join(",", ouvrageIds.stream().map(String::valueOf).toList());

        Mono<List<OuvrageDTO>> response = webClient.get()
                .uri("/ouvrage-service/ouvrages?ids=" + idsParam)
                .retrieve()
                .bodyToFlux(OuvrageDTO.class)
                .collectList();

        return response.block();
    }

    /**
     * Récupère un ouvrage à partir de son identifiant.
     *
     * @param ouvrageId ID de l'ouvrage
     * @return l'OuvrageDTO correspondant, ou null si l'ID est null
     */
    public OuvrageDTO getOuvrageById(Long ouvrageId) {
        if (ouvrageId == null) {
            return null;
        }

        return webClient.get()
                .uri("/ouvrage-service/ouvrages/" + ouvrageId)
                .retrieve()
                .bodyToMono(OuvrageDTO.class)
                .block();
    }
}
