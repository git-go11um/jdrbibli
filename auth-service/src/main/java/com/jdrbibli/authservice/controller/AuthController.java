package com.jdrbibli.authservice.controller;

import com.jdrbibli.authservice.dto.InscriptionRequest;
import com.jdrbibli.authservice.dto.LoginRequest;
import com.jdrbibli.authservice.dto.UserResponseDTO;
import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.service.IUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IUserService userService;

    @PostMapping("/inscription")
    public ResponseEntity<User> inscrireUser(@Valid @RequestBody InscriptionRequest request) {
        User nouvelUser = userService.inscrireNewUser(
                request.getPseudo(),
                request.getEmail(),
                request.getMotDePasse());
        return ResponseEntity.ok(nouvelUser);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody LoginRequest request) {
        User user = userService.login(request.getEmail(), request.getMotDePasse());
        UserResponseDTO dto = userService.toDTO(user);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerUser(@PathVariable Long id) {
        userService.supprimerUser(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
