package com.jdrbibli.userservice.service;

import com.jdrbibli.userservice.dto.OuvrageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class UserLudothequeServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private ResponseSpec responseSpec;

    private UserLudothequeService userLudothequeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userLudothequeService = new UserLudothequeService(webClient);
    }

    @Test
    public void testGetOuvragesByIds_success() {
        // Prépare la donnée de test
        OuvrageDTO ouvrage = new OuvrageDTO();
        ouvrage.setId(1L);
        ouvrage.setTitre("Test Ouvrage");

        List<Long> ids = List.of(1L);

        // Utilisation de doReturn au lieu de when().thenReturn()
        doReturn(requestHeadersUriSpec).when(webClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString());
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doReturn(Flux.just(ouvrage)).when(responseSpec).bodyToFlux(OuvrageDTO.class);

        List<OuvrageDTO> result = userLudothequeService.getOuvragesByIds(ids);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Ouvrage", result.get(0).getTitre());

        verify(webClient).get();
        verify(requestHeadersUriSpec).uri("/ouvrage-service/ouvrages?ids=1");
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToFlux(OuvrageDTO.class);
    }

}
