package com.jdrbibli.userservice.service;

import com.jdrbibli.userservice.dto.OuvrageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
class UserLudothequeServiceTest {

    private WebClient webClient;
    private UserLudothequeService service;

    // Mocks intermédiaires de WebClient (types bruts)
    private WebClient.RequestHeadersUriSpec uriSpecMock;
    private WebClient.RequestHeadersSpec headersSpecMock;
    private WebClient.ResponseSpec responseSpecMock;

    @BeforeEach
    void setUp() {
        webClient = mock(WebClient.class);
        service = new UserLudothequeService(webClient);

        uriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        headersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        responseSpecMock = mock(WebClient.ResponseSpec.class);
    }

    @Test
    void getOuvragesByIds_shouldReturnListOfOuvrages() {
        List<Long> ids = List.of(1L, 2L);

        OuvrageDTO o1 = new OuvrageDTO();
        o1.setId(1L);
        OuvrageDTO o2 = new OuvrageDTO();
        o2.setId(2L);

        when(webClient.get()).thenReturn(uriSpecMock);
        when(uriSpecMock.uri("/ouvrage-service/ouvrages?ids=1,2")).thenReturn(uriSpecMock);
        when(uriSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToFlux(OuvrageDTO.class)).thenReturn(Flux.just(o1, o2));

        List<OuvrageDTO> result = service.getOuvragesByIds(ids);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void getOuvragesByIds_withEmptyList_shouldReturnEmptyList() {
        List<OuvrageDTO> result = service.getOuvragesByIds(List.of());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getOuvrageById_shouldReturnOuvrage() {
        Long id = 1L;
        OuvrageDTO o = new OuvrageDTO();
        o.setId(id);

        when(webClient.get()).thenReturn(uriSpecMock);
        when(uriSpecMock.uri("/ouvrage-service/ouvrages/1")).thenReturn(uriSpecMock);
        when(uriSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(OuvrageDTO.class)).thenReturn(Mono.just(o));

        OuvrageDTO result = service.getOuvrageById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void getOuvrageById_withNull_shouldReturnNull() {
        OuvrageDTO result = service.getOuvrageById(null);
        assertNull(result);
    }
}
