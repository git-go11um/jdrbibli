package com.jdrbibli.ouvrage_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdrbibli.ouvrage_service.dto.OuvrageDTO;
import com.jdrbibli.ouvrage_service.entity.Ouvrage;
import com.jdrbibli.ouvrage_service.mapper.OuvrageMapper;
import com.jdrbibli.ouvrage_service.service.OuvrageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OuvrageControllerTest {

    @Mock
    private OuvrageService ouvrageService;

    @Mock
    private OuvrageMapper ouvrageMapper;

    @InjectMocks
    private OuvrageController ouvrageController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(ouvrageController).build();
    }

    @Test
    void testGetAll_success() throws Exception {
        String ownerPseudo = "user1";
        Ouvrage ouvrage = new Ouvrage();
        ouvrage.setOwnerPseudo(ownerPseudo);

        OuvrageDTO dto = new OuvrageDTO();
        dto.setOwnerPseudo(ownerPseudo);

        when(ouvrageService.findByOwnerPseudo(ownerPseudo)).thenReturn(List.of(ouvrage));
        when(ouvrageMapper.toDTO(any(Ouvrage.class))).thenReturn(dto);

        mockMvc.perform(get("/api/ouvrage/ouvrages")
                .header("X-User-Pseudo", ownerPseudo))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(ouvrageService).findByOwnerPseudo(ownerPseudo);
    }

    @Test
    void testGetById_success() throws Exception {
        Long id = 1L;
        String ownerPseudo = "user1";

        Ouvrage ouvrage = new Ouvrage();
        ouvrage.setOwnerPseudo(ownerPseudo);

        OuvrageDTO dto = new OuvrageDTO();
        dto.setOwnerPseudo(ownerPseudo);

        when(ouvrageService.findById(id)).thenReturn(Optional.of(ouvrage));
        when(ouvrageMapper.toDTO(ouvrage)).thenReturn(dto);

        mockMvc.perform(get("/api/ouvrage/ouvrages/{id}", id)
                .header("X-User-Pseudo", ownerPseudo))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(ouvrageService).findById(id);
    }

    @Test
    void testGetById_forbidden() throws Exception {
        Long id = 1L;
        String ownerPseudo = "user1";

        Ouvrage ouvrage = new Ouvrage();
        ouvrage.setOwnerPseudo("otherUser");

        when(ouvrageService.findById(id)).thenReturn(Optional.of(ouvrage));

        mockMvc.perform(get("/api/ouvrage/ouvrages/{id}", id)
                .header("X-User-Pseudo", ownerPseudo))
                .andExpect(status().isForbidden());

        verify(ouvrageService).findById(id);
    }

    @Test
    void testGetById_notFound() throws Exception {
        Long id = 1L;
        String ownerPseudo = "user1";

        when(ouvrageService.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/ouvrage/ouvrages/{id}", id)
                .header("X-User-Pseudo", ownerPseudo))
                .andExpect(status().isNotFound());

        verify(ouvrageService).findById(id);
    }

    @Test
    void testCreate_success() throws Exception {
        String ownerPseudo = "user1";
        OuvrageDTO inputDTO = new OuvrageDTO();
        Ouvrage createdEntity = new Ouvrage();
        createdEntity.setOwnerPseudo(ownerPseudo);

        OuvrageDTO returnedDTO = new OuvrageDTO();
        returnedDTO.setOwnerPseudo(ownerPseudo);

        when(ouvrageService.createFromDTO(any(OuvrageDTO.class))).thenReturn(createdEntity);
        when(ouvrageMapper.toDTO(createdEntity)).thenReturn(returnedDTO);

        mockMvc.perform(post("/api/ouvrage/ouvrages")
                .header("X-User-Pseudo", ownerPseudo)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // Vérifier que ownerPseudo a bien été assigné dans DTO avant création
        ArgumentCaptor<OuvrageDTO> dtoCaptor = ArgumentCaptor.forClass(OuvrageDTO.class);
        verify(ouvrageService).createFromDTO(dtoCaptor.capture());
        assert (dtoCaptor.getValue().getOwnerPseudo().equals(ownerPseudo));
    }

    @Test
    void testCreate_failure() throws Exception {
        String ownerPseudo = "user1";
        OuvrageDTO inputDTO = new OuvrageDTO();

        when(ouvrageService.createFromDTO(any(OuvrageDTO.class))).thenThrow(new RuntimeException("Erreur"));

        mockMvc.perform(post("/api/ouvrage/ouvrages")
                .header("X-User-Pseudo", ownerPseudo)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdate_success() throws Exception {
        Long id = 1L;
        String ownerPseudo = "user1";

        Ouvrage existing = new Ouvrage();
        existing.setOwnerPseudo(ownerPseudo);

        OuvrageDTO inputDTO = new OuvrageDTO();
        inputDTO.setOwnerPseudo(ownerPseudo);

        Ouvrage updated = new Ouvrage();
        updated.setOwnerPseudo(ownerPseudo);

        OuvrageDTO returnedDTO = new OuvrageDTO();
        returnedDTO.setOwnerPseudo(ownerPseudo);

        when(ouvrageService.findById(id)).thenReturn(Optional.of(existing));
        when(ouvrageService.updateFromDTO(eq(id), any(OuvrageDTO.class))).thenReturn(updated);
        when(ouvrageMapper.toDTO(updated)).thenReturn(returnedDTO);

        mockMvc.perform(put("/api/ouvrage/ouvrages/{id}", id)
                .header("X-User-Pseudo", ownerPseudo)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isOk());

        verify(ouvrageService).updateFromDTO(eq(id), any(OuvrageDTO.class));
    }

    @Test
    void testUpdate_notFound() throws Exception {
        Long id = 1L;
        String ownerPseudo = "user1";

        when(ouvrageService.findById(id)).thenReturn(Optional.empty());

        OuvrageDTO inputDTO = new OuvrageDTO();

        mockMvc.perform(put("/api/ouvrage/ouvrages/{id}", id)
                .header("X-User-Pseudo", ownerPseudo)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdate_forbidden() throws Exception {
        Long id = 1L;
        String ownerPseudo = "user1";

        Ouvrage existing = new Ouvrage();
        existing.setOwnerPseudo("otherUser");

        when(ouvrageService.findById(id)).thenReturn(Optional.of(existing));

        OuvrageDTO inputDTO = new OuvrageDTO();

        mockMvc.perform(put("/api/ouvrage/ouvrages/{id}", id)
                .header("X-User-Pseudo", ownerPseudo)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdate_failure() throws Exception {
        Long id = 1L;
        String ownerPseudo = "user1";

        Ouvrage existing = new Ouvrage();
        existing.setOwnerPseudo(ownerPseudo);

        OuvrageDTO inputDTO = new OuvrageDTO();

        when(ouvrageService.findById(id)).thenReturn(Optional.of(existing));
        when(ouvrageService.updateFromDTO(eq(id), any(OuvrageDTO.class))).thenThrow(new RuntimeException("Erreur"));

        mockMvc.perform(put("/api/ouvrage/ouvrages/{id}", id)
                .header("X-User-Pseudo", ownerPseudo)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDelete_success() throws Exception {
        Long id = 1L;
        String ownerPseudo = "user1";

        Ouvrage existing = new Ouvrage();
        existing.setOwnerPseudo(ownerPseudo);

        when(ouvrageService.findById(id)).thenReturn(Optional.of(existing));
        doNothing().when(ouvrageService).deleteById(id);

        mockMvc.perform(delete("/api/ouvrage/ouvrages/{id}", id)
                .header("X-User-Pseudo", ownerPseudo))
                .andExpect(status().isNoContent());

        verify(ouvrageService).deleteById(id);
    }

    @Test
    void testDelete_notFound() throws Exception {
        Long id = 1L;
        String ownerPseudo = "user1";

        when(ouvrageService.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/ouvrage/ouvrages/{id}", id)
                .header("X-User-Pseudo", ownerPseudo))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDelete_forbidden() throws Exception {
        Long id = 1L;
        String ownerPseudo = "user1";

        Ouvrage existing = new Ouvrage();
        existing.setOwnerPseudo("otherUser");

        when(ouvrageService.findById(id)).thenReturn(Optional.of(existing));

        mockMvc.perform(delete("/api/ouvrage/ouvrages/{id}", id)
                .header("X-User-Pseudo", ownerPseudo))
                .andExpect(status().isForbidden());
    }
}
