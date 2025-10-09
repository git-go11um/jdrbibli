package com.jdrbibli.ouvrage_service.controller;

import com.jdrbibli.ouvrage_service.dto.OuvrageDTO;
import com.jdrbibli.ouvrage_service.entity.Ouvrage;
import com.jdrbibli.ouvrage_service.mapper.OuvrageMapper;
import com.jdrbibli.ouvrage_service.service.OuvrageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class OuvrageControllerTest {

    private OuvrageService ouvrageService;
    private OuvrageMapper ouvrageMapper;
    private OuvrageController controller;

    @BeforeEach
    void setUp() {
        ouvrageService = mock(OuvrageService.class);
        ouvrageMapper = mock(OuvrageMapper.class);
        controller = new OuvrageController(ouvrageService, ouvrageMapper);
    }

    @Test
    void getById_returnsOuvrageDTO_whenOwnerMatches() {
        Ouvrage ouvrage = new Ouvrage();
        ouvrage.setId(1L);
        ouvrage.setTitre("Ouvrage1"); // <-- correction ici
        ouvrage.setOwnerId(42L);

        OuvrageDTO dto = new OuvrageDTO();
        dto.setId(1L);
        dto.setTitre("Ouvrage1"); // si ton DTO a encore "nom"

        when(ouvrageService.findById(1L)).thenReturn(Optional.of(ouvrage));
        when(ouvrageMapper.toDTO(ouvrage)).thenReturn(dto);

        ResponseEntity<OuvrageDTO> response = controller.getById(1L, 42L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(dto);

        verify(ouvrageService, times(1)).findById(1L);
        verify(ouvrageMapper, times(1)).toDTO(ouvrage);
    }

    @Test
    void getById_returnsForbidden_whenOwnerMismatch() {
        Ouvrage ouvrage = new Ouvrage();
        ouvrage.setId(1L);
        ouvrage.setTitre("Ouvrage1");
        ouvrage.setOwnerId(42L);

        when(ouvrageService.findById(1L)).thenReturn(Optional.of(ouvrage));

        ResponseEntity<OuvrageDTO> response = controller.getById(1L, 99L);

        assertThat(response.getStatusCodeValue()).isEqualTo(403);

        verify(ouvrageService, times(1)).findById(1L);
        verifyNoInteractions(ouvrageMapper);
    }

    @Test
    void getById_returnsNotFound_whenOuvrageMissing() {
        when(ouvrageService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<OuvrageDTO> response = controller.getById(1L, 42L);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        verify(ouvrageService, times(1)).findById(1L);
        verifyNoInteractions(ouvrageMapper);
    }

    

}
