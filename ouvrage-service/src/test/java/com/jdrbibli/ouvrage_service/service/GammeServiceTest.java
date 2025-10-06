package com.jdrbibli.ouvrage_service.service;

import com.jdrbibli.ouvrage_service.entity.Gamme;
import com.jdrbibli.ouvrage_service.entity.Ouvrage;
import com.jdrbibli.ouvrage_service.exception.ResourceNotFoundException;
import com.jdrbibli.ouvrage_service.mapper.GammeMapper;
import com.jdrbibli.ouvrage_service.repository.GammeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GammeServiceTest {

    @Mock
    private GammeRepository gammeRepository;

    @Mock
    private GammeMapper gammeMapper;

    @InjectMocks
    private GammeService gammeService;

    private Gamme gamme;

    @BeforeEach
    void setUp() {
        gamme = new Gamme();
        gamme.setId(1L);
        gamme.setNom("Test Gamme");
        gamme.setOwnerId(42L);
        gamme.setOuvrages(new ArrayList<>());
    }

    @Test
    void save_shouldSaveGamme_whenOwnerIdIsSet() {
        when(gammeRepository.save(gamme)).thenReturn(gamme);

        Gamme saved = gammeService.save(gamme);

        assertEquals(gamme, saved);
        verify(gammeRepository).save(gamme);
    }

    @Test
    void save_shouldThrow_whenOwnerIdIsNull() {
        gamme.setOwnerId(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> gammeService.save(gamme));
        assertEquals("OwnerId must be set", exception.getMessage());
    }

    @Test
    void findByOwnerId_shouldReturnListOfGammes() {
        List<Gamme> gammes = Arrays.asList(gamme);
        when(gammeRepository.findByOwnerId(42L)).thenReturn(gammes);

        List<Gamme> result = gammeService.findByOwnerId(42L);

        assertEquals(1, result.size());
        assertEquals(gamme, result.get(0));
    }

    @Test
    void findById_shouldReturnGamme_whenExists() {
        when(gammeRepository.findById(1L)).thenReturn(Optional.of(gamme));

        Optional<Gamme> result = gammeService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(gamme, result.get());
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        when(gammeRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Gamme> result = gammeService.findById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void deleteById_shouldThrow_whenGammeNotFound() {
        when(gammeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> gammeService.deleteById(1L, true));
    }

    @Test
    void deleteById_shouldThrow_whenGammeHasOuvrages_andForceFalse() {
        gamme.getOuvrages().add(new Ouvrage());
        when(gammeRepository.findById(1L)).thenReturn(Optional.of(gamme));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> gammeService.deleteById(1L, false));
        assertEquals("La gamme contient des ouvrages. Utilisez force=true pour supprimer.", exception.getMessage());
    }

    @Test
    void deleteById_shouldDelete_whenForceTrue() {
        gamme.getOuvrages().add(new Ouvrage());
        when(gammeRepository.findById(1L)).thenReturn(Optional.of(gamme));

        gammeService.deleteById(1L, true);

        verify(gammeRepository).delete(gamme);
    }

    @Test
    void deleteByOwnerId_shouldDeleteAllGammesOfOwner() {
        Gamme autreGamme = new Gamme();
        autreGamme.setId(2L);
        autreGamme.setOwnerId(42L);
        List<Gamme> gammes = Arrays.asList(gamme, autreGamme);

        when(gammeRepository.findByOwnerId(42L)).thenReturn(gammes);

        gammeService.deleteByOwnerId(42L);

        verify(gammeRepository, times(1)).delete(gamme);
        verify(gammeRepository, times(1)).delete(autreGamme);
    }
}
