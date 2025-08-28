package com.jdrbibli.userservice.service;

import com.jdrbibli.userservice.dto.OuvrageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class UserLudothequeService {

    private final WebClient webClient;

    @Autowired
    public UserLudothequeService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Récupérer la liste des ouvrages complets depuis ouvrage-service via gateway.
     * 
     * @param ouvrageIds Liste des IDs à chercher
     * @return Liste de OuvrageDTO
     */
    public List<OuvrageDTO> getOuvragesByIds(List<Long> ouvrageIds) {
        if (ouvrageIds == null || ouvrageIds.isEmpty()) {
            return List.of();
        }

        // Transforme liste en string "1,2,3"
        String idsParam = String.join(",", ouvrageIds.stream().map(String::valueOf).toList());

        // Appelle via le gateway
        Mono<List<OuvrageDTO>> response = webClient.get()
                .uri("/ouvrage-service/ouvrages?ids=" + idsParam)
                .retrieve()
                .bodyToFlux(OuvrageDTO.class)
                .collectList();

        return response.block(); // blocage pour simplifier, tu pourras plus tard le rendre async
    }

    /**
     * Récupérer un ouvrage unique par son ID via gateway.
     * 
     * @param ouvrageId ID de l'ouvrage
     * @return OuvrageDTO ou null si non trouvé
     */
    public OuvrageDTO getOuvrageById(Long ouvrageId) {
        if (ouvrageId == null) {
            return null;
        }

        return webClient.get()
                .uri("/ouvrage-service/ouvrages/" + ouvrageId)
                .retrieve()
                .bodyToMono(OuvrageDTO.class)
                .block(); // blocage pour simplifier
    }
}
