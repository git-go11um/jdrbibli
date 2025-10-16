package com.jdrbibli.ouvrage_service.controller;

import com.jdrbibli.ouvrage_service.dto.OuvrageDTO;
import com.jdrbibli.ouvrage_service.entity.Ouvrage;
import com.jdrbibli.ouvrage_service.mapper.OuvrageMapper;
import com.jdrbibli.ouvrage_service.service.OuvrageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

    // ---------- GET BY ID ----------
    @Test
    void getById_ownerMatch_returnsDTO() {
        Ouvrage o = new Ouvrage();
        o.setId(1L); o.setOwnerId(42L); o.setTitre("T1");
        OuvrageDTO dto = new OuvrageDTO(); dto.setId(1L); dto.setOwnerId(42L); dto.setTitre("T1");

        when(ouvrageService.findById(1L)).thenReturn(Optional.of(o));
        when(ouvrageMapper.toDTO(o)).thenReturn(dto);

        ResponseEntity<OuvrageDTO> res = controller.getById(1L, 42L);
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(res.getBody()).isEqualTo(dto);
    }

    @Test
    void getById_ownerMismatch_returns403() {
        Ouvrage o = new Ouvrage(); o.setId(1L); o.setOwnerId(42L);
        when(ouvrageService.findById(1L)).thenReturn(Optional.of(o));
        ResponseEntity<OuvrageDTO> res = controller.getById(1L, 99L);
        assertThat(res.getStatusCodeValue()).isEqualTo(403);
        verifyNoInteractions(ouvrageMapper);
    }

    @Test
    void getById_notFound_returns404() {
        when(ouvrageService.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<OuvrageDTO> res = controller.getById(1L, 42L);
        assertThat(res.getStatusCodeValue()).isEqualTo(404);
        verifyNoInteractions(ouvrageMapper);
    }

    // ---------- GET ALL ----------
    @Test
    void getAll_returnsList() {
        Ouvrage o = new Ouvrage(); o.setId(1L); o.setOwnerId(42L);
        OuvrageDTO dto = new OuvrageDTO(); dto.setId(1L); dto.setOwnerId(42L);
        when(ouvrageService.findByOwnerId(42L)).thenReturn(List.of(o));
        when(ouvrageMapper.toDTO(o)).thenReturn(dto);

        ResponseEntity<List<OuvrageDTO>> res = controller.getAll(42L);
        assertThat(res.getBody()).containsExactly(dto);
    }

    // ---------- CREATE ----------
    @Test
    void create_returnsCreatedDTO() {
        OuvrageDTO dto = new OuvrageDTO(); dto.setTitre("T1");
        Ouvrage o = new Ouvrage(); o.setTitre("T1"); o.setOwnerId(42L);
        OuvrageDTO returnedDto = new OuvrageDTO(); returnedDto.setTitre("T1"); returnedDto.setOwnerId(42L);

        when(ouvrageService.createFromDTO(any())).thenReturn(o);
        when(ouvrageMapper.toDTO(o)).thenReturn(returnedDto);

        ResponseEntity<OuvrageDTO> res = controller.create(dto, 42L);
        assertThat(res.getStatusCodeValue()).isEqualTo(201);
        assertThat(res.getBody().getOwnerId()).isEqualTo(42L);
    }

    // ---------- UPDATE ----------
    @Test
    void update_success() {
        OuvrageDTO dto = new OuvrageDTO(); dto.setTitre("T2");
        Ouvrage existing = new Ouvrage(); existing.setId(1L); existing.setOwnerId(42L);
        Ouvrage updated = new Ouvrage(); updated.setId(1L); updated.setOwnerId(42L);

        when(ouvrageService.findById(1L)).thenReturn(Optional.of(existing));
        when(ouvrageService.updateFromDTO(1L, dto)).thenReturn(updated);
        when(ouvrageMapper.toDTO(updated)).thenReturn(dto);

        ResponseEntity<OuvrageDTO> res = controller.update(1L, dto, 42L);
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void update_notFound_returns404() {
        when(ouvrageService.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<OuvrageDTO> res = controller.update(1L, new OuvrageDTO(), 42L);
        assertThat(res.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    void update_forbidden_returns403() {
        Ouvrage existing = new Ouvrage(); existing.setId(1L); existing.setOwnerId(42L);
        when(ouvrageService.findById(1L)).thenReturn(Optional.of(existing));
        ResponseEntity<OuvrageDTO> res = controller.update(1L, new OuvrageDTO(), 99L);
        assertThat(res.getStatusCodeValue()).isEqualTo(403);
    }

    // ---------- DELETE ----------
    @Test
    void delete_success() {
        Ouvrage o = new Ouvrage(); o.setId(1L); o.setOwnerId(42L);
        when(ouvrageService.findById(1L)).thenReturn(Optional.of(o));

        ResponseEntity<Void> res = controller.delete(1L, 42L);
        assertThat(res.getStatusCodeValue()).isEqualTo(204);
        verify(ouvrageService).deleteById(1L);
    }

    @Test
    void delete_notFound_returns404() {
        when(ouvrageService.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<Void> res = controller.delete(1L, 42L);
        assertThat(res.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    void delete_forbidden_returns403() {
        Ouvrage o = new Ouvrage(); o.setId(1L); o.setOwnerId(42L);
        when(ouvrageService.findById(1L)).thenReturn(Optional.of(o));
        ResponseEntity<Void> res = controller.delete(1L, 99L);
        assertThat(res.getStatusCodeValue()).isEqualTo(403);
    }

    // ---------- UPLOAD IMAGE ----------
    @Test
    void uploadImage_success() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("img.png");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{1,2,3}));

        ResponseEntity<String> res = controller.uploadImage(file);
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(res.getBody()).contains("/uploads/images/");
    }

    @Test
    void uploadImage_empty_returns400() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);
        ResponseEntity<String> res = controller.uploadImage(file);
        assertThat(res.getStatusCodeValue()).isEqualTo(400);
    }

    // ---------- FRIEND LIBRARY ----------
    @Test
    void getFriendLibrary_success() {
        // Ici, on mock WebClient ou on refactore controller pour passer WebClient injectable
        // Sinon coverage difficile
    }

    // ---------- DTO GETTERS / SETTERS ----------
    @Test
    void dtoGettersSetters() {
        OuvrageDTO dto = new OuvrageDTO();
        dto.setId(1L); dto.setTitre("T"); dto.setOwnerId(42L);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTitre()).isEqualTo("T");
        assertThat(dto.getOwnerId()).isEqualTo(42L);
    }
}
