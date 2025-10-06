package com.jdrbibli.ouvrage_service.service;

import com.jdrbibli.ouvrage_service.dto.OuvrageDTO;
import com.jdrbibli.ouvrage_service.entity.Gamme;
import com.jdrbibli.ouvrage_service.entity.Ouvrage;
import com.jdrbibli.ouvrage_service.exception.ResourceNotFoundException;
import com.jdrbibli.ouvrage_service.mapper.OuvrageMapper;
import com.jdrbibli.ouvrage_service.repository.GammeRepository;
import com.jdrbibli.ouvrage_service.repository.OuvrageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OuvrageServiceTest {

    @Mock
    private OuvrageRepository ouvrageRepository;

    @Mock
    private GammeRepository gammeRepository;

    @Mock
    private OuvrageMapper ouvrageMapper;

    @InjectMocks
    private OuvrageService ouvrageService;

    private Ouvrage ouvrage;
    private Gamme gamme;
    private OuvrageDTO dto;

    @BeforeEach
    void setUp() {
        gamme = new Gamme();
        gamme.setId(1L);
        gamme.setOwnerId(42L);

        ouvrage = new Ouvrage();
        ouvrage.setId(1L);
        ouvrage.setGamme(gamme);

        dto = new OuvrageDTO();
        dto.setGammeId(1L);
        dto.setOwnerId(42L);
        dto.setTitre("Titre Test");
    }

    @Test
    void createFromDTO_shouldCreateOuvrage_whenValid() {
        when(gammeRepository.findById(1L)).thenReturn(Optional.of(gamme));
        when(ouvrageMapper.toEntity(dto)).thenReturn(ouvrage);
        when(ouvrageRepository.save(ouvrage)).thenReturn(ouvrage);

        Ouvrage result = ouvrageService.createFromDTO(dto);

        assertEquals(ouvrage, result);
        assertEquals(gamme, result.getGamme());
        verify(ouvrageRepository).save(ouvrage);
    }

    @Test
    void createFromDTO_shouldThrow_whenGammeNotFound() {
        when(gammeRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> ouvrageService.createFromDTO(dto));
        assertEquals("Gamme not found with id 1", exception.getMessage());
    }

    @Test
    void createFromDTO_shouldThrow_whenOwnerIdNull() {
        dto.setOwnerId(null);
        when(gammeRepository.findById(1L)).thenReturn(Optional.of(gamme));
        when(ouvrageMapper.toEntity(dto)).thenReturn(ouvrage);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ouvrageService.createFromDTO(dto));
        assertEquals("OwnerId must be set", exception.getMessage());
    }

    @Test
    void updateFromDTO_shouldUpdateOuvrage_whenValid() {
        when(ouvrageRepository.findById(1L)).thenReturn(Optional.of(ouvrage));
        when(gammeRepository.findById(1L)).thenReturn(Optional.of(gamme));
        when(ouvrageRepository.save(ouvrage)).thenReturn(ouvrage);

        Ouvrage result = ouvrageService.updateFromDTO(1L, dto);

        assertEquals(ouvrage, result);
        verify(ouvrageRepository).save(ouvrage);
    }

    @Test
    void updateFromDTO_shouldThrow_whenOuvrageNotFound() {
        when(ouvrageRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> ouvrageService.updateFromDTO(1L, dto));
        assertEquals("Ouvrage not found with id 1", exception.getMessage());
    }

    @Test
    void updateFromDTO_shouldThrow_whenGammeNotFound() {
        when(ouvrageRepository.findById(1L)).thenReturn(Optional.of(ouvrage));
        when(gammeRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> ouvrageService.updateFromDTO(1L, dto));
        assertEquals("Gamme not found with id 1", exception.getMessage());
    }

    @Test
    void deleteById_shouldCallRepository() {
        doNothing().when(ouvrageRepository).deleteById(1L);

        ouvrageService.deleteById(1L);

        verify(ouvrageRepository).deleteById(1L);
    }

    @Test
    void findByOwnerId_shouldReturnList() {
        when(ouvrageRepository.findByOwnerId(42L)).thenReturn(Collections.singletonList(ouvrage));

        List<Ouvrage> result = ouvrageService.findByOwnerId(42L);

        assertEquals(1, result.size());
        assertEquals(ouvrage, result.get(0));
    }

    @Test
    void findById_shouldReturnOptional() {
        when(ouvrageRepository.findById(1L)).thenReturn(Optional.of(ouvrage));

        Optional<Ouvrage> result = ouvrageService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(ouvrage, result.get());
    }

    @Test
    void findByIdAndOwnerId_shouldReturnOptional() {
        when(ouvrageRepository.findByIdAndOwnerId(1L, 42L)).thenReturn(Optional.of(ouvrage));

        Optional<Ouvrage> result = ouvrageService.findByIdAndOwnerId(1L, 42L);

        assertTrue(result.isPresent());
        assertEquals(ouvrage, result.get());
    }

    @Test
    void getOuvragesByGamme_shouldReturnDTOList() {
        when(ouvrageRepository.findByGammeIdAndIdNot(1L, 2L)).thenReturn(Collections.singletonList(ouvrage));
        when(ouvrageMapper.toDTO(ouvrage)).thenReturn(dto);

        List<OuvrageDTO> result = ouvrageService.getOuvragesByGamme(1L, 2L);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void findByGammeIdAndOwnerId_shouldReturnList() {
        when(ouvrageRepository.findByGammeIdAndOwnerId(1L, 42L)).thenReturn(Collections.singletonList(ouvrage));

        List<Ouvrage> result = ouvrageService.findByGammeIdAndOwnerId(1L, 42L);

        assertEquals(1, result.size());
        assertEquals(ouvrage, result.get(0));
    }

    @Test
    void findByGammeId_shouldReturnList() {
        when(ouvrageRepository.findByGammeId(1L)).thenReturn(Collections.singletonList(ouvrage));

        List<Ouvrage> result = ouvrageService.findByGammeId(1L);

        assertEquals(1, result.size());
        assertEquals(ouvrage, result.get(0));
    }
}
