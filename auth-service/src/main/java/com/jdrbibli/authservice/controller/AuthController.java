package com.jdrbibli.authservice.controller;

import com.jdrbibli.authservice.dto.InscriptionRequest;
import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IUserService userService;

    @PostMapping("/inscription")
    public ResponseEntity<User> inscrireUser(@Valid @RequestBody InscriptionRequest request) {
        User nouvelUser = userService.inscrireNewUser(
                request.getPseudo(),
                request.getEmail(),
                request.getMotDePasse()
        );
        return ResponseEntity.ok(nouvelUser);
    }
}
