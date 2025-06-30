package com.jdrbibli.userservice.service;

import com.jdrbibli.userservice.dto.OuvrageDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class UserLudothequeService {

    private final WebClient webClient;

    public UserLudothequeService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Récupérer la liste des ouvrages complets depuis ouvrage-service via gateway.
     * @param ouvrageIds Liste des IDs à chercher
     * @return Liste de OuvrageDTO
     */
    public List<OuvrageDTO> getOuvragesByIds(List<Long> ouvrageIds) {
        // Ici on imagine que tu as un endpoint GET dans ouvrage-service comme :
        // GET /ouvrages?ids=1,2,3

        // Transforme liste en string "1,2,3"
        String idsParam = String.join(",", ouvrageIds.stream().map(String::valueOf).toList());

        // Appelle via le gateway
        Mono<List<OuvrageDTO>> response = webClient.get()
                .uri("/ouvrage-service/ouvrages?ids=" + idsParam)
                .retrieve()
                .bodyToFlux(OuvrageDTO.class)
                .collectList();

        return response.block(); //  block() => pour faire simple ici, à adapter en asynchrone plus tard
    }
    public OuvrageDTO getOuvrageById(Long ouvrageId) {
    return webClient.get()
            .uri("/ouvrage-service/ouvrages/" + ouvrageId) // via gateway
            .retrieve()
            .bodyToMono(OuvrageDTO.class)
            .block(); // blocage pour simplifier
}
}
