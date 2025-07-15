package com.jdrbibli.ouvrage_service.controller;

import com.jdrbibli.ouvrage_service.dto.GammeDTO;
import com.jdrbibli.ouvrage_service.entity.Gamme;
import com.jdrbibli.ouvrage_service.mapper.GammeMapper;
import com.jdrbibli.ouvrage_service.service.GammeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

class GammeControllerTest {

    @Mock
    private GammeService gammeService;

    @Mock
    private GammeMapper gammeMapper;

    @InjectMocks
    private GammeController gammeController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(gammeController).build();
    }

    @Test
    void testGetAll_success() throws Exception {
        String ownerPseudo = "user1";
        List<Gamme> gammes = List.of(new Gamme());
        List<GammeDTO> gammesDTO = List.of(new GammeDTO());

        when(gammeService.findByOwnerPseudo(ownerPseudo)).thenReturn(gammes);
        when(gammeMapper.toDTO(any())).thenReturn(new GammeDTO());

        mockMvc.perform(get("/api/ouvrage/gammes")
                .header("X-User-Pseudo", ownerPseudo))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(gammeService).findByOwnerPseudo(ownerPseudo);
    }

    @Test
    void testGetById_success() throws Exception {
        Long id = 1L;
        String ownerPseudo = "user1";

        Gamme gamme = new Gamme();
        gamme.setOwnerPseudo(ownerPseudo);
        GammeDTO dto = new GammeDTO();

        when(gammeService.findById(id)).thenReturn(Optional.of(gamme));
        when(gammeMapper.toDTO(gamme)).thenReturn(dto);

        mockMvc.perform(get("/api/ouvrage/gammes/{id}", id)
                .header("X-User-Pseudo", ownerPseudo))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(gammeService).findById(id);
    }

    @Test
    void testGetById_forbidden() throws Exception {
        Long id = 1L;
        String ownerPseudo = "user1";

        Gamme gamme = new Gamme();
        gamme.setOwnerPseudo("otherUser");

        when(gammeService.findById(id)).thenReturn(Optional.of(gamme));

        mockMvc.perform(get("/api/ouvrage/gammes/{id}", id)
                .header("X-User-Pseudo", ownerPseudo))
                .andExpect(status().isForbidden());

        verify(gammeService).findById(id);
    }

    @Test
    void testGetById_notFound() throws Exception {
        Long id = 1L;
        String ownerPseudo = "user1";

        when(gammeService.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/ouvrage/gammes/{id}", id)
                .header("X-User-Pseudo", ownerPseudo))
                .andExpect(status().isNotFound());

        verify(gammeService).findById(id);
    }

    @Test
    void testCreate_success() throws Exception {
        String ownerPseudo = "user1";
        GammeDTO inputDTO = new GammeDTO(null, "Nom", "Desc", null);
        Gamme gammeEntity = new Gamme();
        Gamme savedEntity = new Gamme();
        GammeDTO returnedDTO = new GammeDTO();

        when(gammeMapper.toEntity(any(GammeDTO.class))).thenReturn(gammeEntity);
        when(gammeService.save(gammeEntity)).thenReturn(savedEntity);
        when(gammeMapper.toDTO(savedEntity)).thenReturn(returnedDTO);

        mockMvc.perform(post("/api/ouvrage/gammes")
                .header("X-User-Pseudo", ownerPseudo)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // Vérifie que ownerPseudo est forcé dans DTO avant mapping
        ArgumentCaptor<GammeDTO> dtoCaptor = ArgumentCaptor.forClass(GammeDTO.class);
        verify(gammeMapper).toEntity(dtoCaptor.capture());
        assert (dtoCaptor.getValue().getOwnerPseudo().equals(ownerPseudo));
    }

    @Test
    void testUpdate_success() throws Exception {
        Long id = 1L;
        String ownerPseudo = "user1";

        Gamme existing = new Gamme();
        existing.setOwnerPseudo(ownerPseudo);

        GammeDTO updateDTO = new GammeDTO();
        updateDTO.setNom("newNom");
        updateDTO.setDescription("newDesc");

        Gamme saved = new Gamme();

        when(gammeService.findById(id)).thenReturn(Optional.of(existing));
        when(gammeService.save(existing)).thenReturn(saved);

        mockMvc.perform(put("/api/ouvrage/gammes/{id}", id)
                .header("X-User-Pseudo", ownerPseudo)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk());

        verify(gammeService).save(existing);
        // Vérifie que les champs ont bien été modifiés
        assert (existing.getNom().equals("newNom"));
        assert (existing.getDescription().equals("newDesc"));
    }

    @Test
    void testUpdate_forbidden() throws Exception {
        Long id = 1L;
        String ownerPseudo = "user1";

        Gamme existing = new Gamme();
        existing.setOwnerPseudo("otherUser");

        when(gammeService.findById(id)).thenReturn(Optional.of(existing));

        GammeDTO updateDTO = new GammeDTO();

        mockMvc.perform(put("/api/ouvrage/gammes/{id}", id)
                .header("X-User-Pseudo", ownerPseudo)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDelete_success() throws Exception {
        Long id = 1L;
        String ownerPseudo = "user1";

        Gamme existing = new Gamme();
        existing.setOwnerPseudo(ownerPseudo);

        when(gammeService.findById(id)).thenReturn(Optional.of(existing));
        doNothing().when(gammeService).deleteById(id, false);

        mockMvc.perform(delete("/api/ouvrage/gammes/{id}", id)
                .header("X-User-Pseudo", ownerPseudo))
                .andExpect(status().isNoContent());

        verify(gammeService).deleteById(id, false);
    }

    @Test
    void testDelete_forbidden() throws Exception {
        Long id = 1L;
        String ownerPseudo = "user1";

        Gamme existing = new Gamme();
        existing.setOwnerPseudo("otherUser");

        when(gammeService.findById(id)).thenReturn(Optional.of(existing));

        mockMvc.perform(delete("/api/ouvrage/gammes/{id}", id)
                .header("X-User-Pseudo", ownerPseudo))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDelete_notFound() throws Exception {
        Long id = 1L;
        String ownerPseudo = "user1";

        when(gammeService.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/ouvrage/gammes/{id}", id)
                .header("X-User-Pseudo", ownerPseudo))
                .andExpect(status().isNotFound());
    }

}
