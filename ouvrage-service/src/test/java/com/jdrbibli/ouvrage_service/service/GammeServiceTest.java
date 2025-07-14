package com.jdrbibli.ouvrage_service.service;

import com.jdrbibli.ouvrage_service.entity.Gamme;
import com.jdrbibli.ouvrage_service.mapper.GammeMapper;
import com.jdrbibli.ouvrage_service.repository.GammeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GammeServiceTest {

    @Mock
    private GammeRepository gammeRepository;

    @Mock
    private GammeMapper gammeMapper;

    @InjectMocks
    private GammeService gammeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByOwnerPseudo_returnsList() {
        String owner = "user1";
        List<Gamme> expectedList = List.of(new Gamme(), new Gamme());
        when(gammeRepository.findByOwnerPseudo(owner)).thenReturn(expectedList);

        List<Gamme> actualList = gammeService.findByOwnerPseudo(owner);

        assertEquals(expectedList, actualList);
        verify(gammeRepository, times(1)).findByOwnerPseudo(owner);
    }

    @Test
    void testFindById_found() {
        Gamme gamme = new Gamme();
        gamme.setId(1L);
        when(gammeRepository.findById(1L)).thenReturn(Optional.of(gamme));

        Optional<Gamme> result = gammeService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(gamme, result.get());
        verify(gammeRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_notFound() {
        when(gammeRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Gamme> result = gammeService.findById(999L);

        assertFalse(result.isPresent());
        verify(gammeRepository, times(1)).findById(999L);
    }

    @Test
    void testSave_withValidOwnerPseudo() {
        Gamme gamme = new Gamme();
        gamme.setOwnerPseudo("owner1");
        when(gammeRepository.save(gamme)).thenReturn(gamme);

        Gamme saved = gammeService.save(gamme);

        assertEquals(gamme, saved);
        verify(gammeRepository, times(1)).save(gamme);
    }

    @Test
    void testSave_withoutOwnerPseudo_throwsException() {
        Gamme gamme = new Gamme();
        gamme.setOwnerPseudo(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            gammeService.save(gamme);
        });
        assertEquals("OwnerPseudo must be set", ex.getMessage());

        gamme.setOwnerPseudo("   "); // blank string

        ex = assertThrows(IllegalArgumentException.class, () -> {
            gammeService.save(gamme);
        });
        assertEquals("OwnerPseudo must be set", ex.getMessage());

        verify(gammeRepository, never()).save(any());
    }

    @Test
    void testDeleteById_callsRepositoryDelete() {
        Long id = 10L;
        gammeService.deleteById(id, true);
        verify(gammeRepository, times(1)).deleteById(id);
    }
}
