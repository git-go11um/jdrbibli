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
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OuvrageServiceTest {

    @Mock
    private OuvrageRepository ouvrageRepository;

    @Mock
    private GammeRepository gammeRepository;

    @Mock
    private OuvrageMapper ouvrageMapper;

    @InjectMocks
    private OuvrageService ouvrageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByOwnerPseudo_returnsList() {
        String owner = "user1";
        List<Ouvrage> expected = List.of(new Ouvrage(), new Ouvrage());
        when(ouvrageRepository.findByOwnerPseudo(owner)).thenReturn(expected);

        List<Ouvrage> actual = ouvrageService.findByOwnerPseudo(owner);

        assertEquals(expected, actual);
        verify(ouvrageRepository, times(1)).findByOwnerPseudo(owner);
    }

    @Test
    void testFindById_found() {
        Ouvrage ouvrage = new Ouvrage();
        ouvrage.setId(1L);
        when(ouvrageRepository.findById(1L)).thenReturn(Optional.of(ouvrage));

        Optional<Ouvrage> result = ouvrageService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(ouvrage, result.get());
        verify(ouvrageRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_notFound() {
        when(ouvrageRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Ouvrage> result = ouvrageService.findById(999L);

        assertFalse(result.isPresent());
        verify(ouvrageRepository, times(1)).findById(999L);
    }

    @Test
    void testCreateFromDTO_success() {
        Gamme gamme = new Gamme();
        gamme.setId(10L);
        OuvrageDTO dto = new OuvrageDTO();
        dto.setGammeId(10L);
        dto.setOwnerPseudo("owner");

        Ouvrage mappedOuvrage = new Ouvrage();
        when(gammeRepository.findById(10L)).thenReturn(Optional.of(gamme));
        when(ouvrageMapper.toEntity(dto)).thenReturn(mappedOuvrage);
        when(ouvrageRepository.save(any(Ouvrage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ouvrage result = ouvrageService.createFromDTO(dto);

        assertNotNull(result);
        assertEquals(gamme, result.getGamme());
        assertEquals("owner", result.getOwnerPseudo());
        verify(gammeRepository, times(1)).findById(10L);
        verify(ouvrageMapper, times(1)).toEntity(dto);
        verify(ouvrageRepository, times(1)).save(result);
    }

    @Test
    void testCreateFromDTO_gammeNotFound_throwsException() {
        OuvrageDTO dto = new OuvrageDTO();
        dto.setGammeId(999L);

        when(gammeRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            ouvrageService.createFromDTO(dto);
        });

        assertEquals("Gamme not found with id 999", ex.getMessage());
        verify(ouvrageRepository, never()).save(any());
    }

    @Test
    void testUpdateFromDTO_success() {
        Long id = 1L;
        Gamme gamme = new Gamme();
        gamme.setId(20L);

        Ouvrage existing = new Ouvrage();
        existing.setId(id);
        existing.setOwnerPseudo("ownerExisting");

        OuvrageDTO dto = new OuvrageDTO();
        dto.setGammeId(20L);
        dto.setOwnerPseudo("ownerNew");

        Ouvrage mappedUpdated = new Ouvrage();

        when(ouvrageRepository.findById(id)).thenReturn(Optional.of(existing));
        when(gammeRepository.findById(20L)).thenReturn(Optional.of(gamme));
        when(ouvrageMapper.toEntity(any(OuvrageDTO.class))).thenReturn(mappedUpdated);
        when(ouvrageRepository.save(any(Ouvrage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ouvrage result = ouvrageService.updateFromDTO(id, dto);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(gamme, result.getGamme());
        assertEquals("ownerExisting", dto.getOwnerPseudo()); // dto ownerPseudo est remplacé
        verify(ouvrageRepository, times(1)).findById(id);
        verify(gammeRepository, times(1)).findById(20L);
        verify(ouvrageMapper, times(1)).toEntity(dto);
        verify(ouvrageRepository, times(1)).save(result);
    }

    @Test
    void testUpdateFromDTO_ouvrageNotFound_throwsException() {
        Long id = 999L;
        OuvrageDTO dto = new OuvrageDTO();

        when(ouvrageRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            ouvrageService.updateFromDTO(id, dto);
        });

        assertEquals("Ouvrage not found with id " + id, ex.getMessage());
        verify(gammeRepository, never()).findById(any());
        verify(ouvrageRepository, never()).save(any());
    }

    @Test
    void testUpdateFromDTO_gammeNotFound_throwsException() {
        Long id = 1L;
        Ouvrage existing = new Ouvrage();
        existing.setId(id);

        OuvrageDTO dto = new OuvrageDTO();
        dto.setGammeId(999L);

        when(ouvrageRepository.findById(id)).thenReturn(Optional.of(existing));
        when(gammeRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            ouvrageService.updateFromDTO(id, dto);
        });

        assertEquals("Gamme not found with id 999", ex.getMessage());
        verify(ouvrageRepository, never()).save(any());
    }

    @Test
    void testDeleteById_callsRepositoryDelete() {
        Long id = 10L;
        ouvrageService.deleteById(id);
        verify(ouvrageRepository, times(1)).deleteById(id);
    }
}
