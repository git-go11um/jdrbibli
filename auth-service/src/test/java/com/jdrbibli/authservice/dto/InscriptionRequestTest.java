package com.jdrbibli.authservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class InscriptionRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidInscriptionRequest() {
        InscriptionRequest request = new InscriptionRequest();
        request.setPseudo("monPseudo");
        request.setEmail("test@example.com");
        request.setMotDePasse("motdepasse123");

        Set<ConstraintViolation<InscriptionRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Aucune violation attendue");
    }

    @Test
    void testInvalidInscriptionRequest() {
        InscriptionRequest request = new InscriptionRequest();
        request.setPseudo(""); // violation NotBlank
        request.setEmail("invalid-email"); // violation Email
        request.setMotDePasse(null); // violation NotBlank

        Set<ConstraintViolation<InscriptionRequest>> violations = validator.validate(request);
        assertEquals(3, violations.size());

        boolean pseudoViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("pseudo"));
        boolean emailViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
        boolean motDePasseViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("motDePasse"));

        assertTrue(pseudoViolation);
        assertTrue(emailViolation);
        assertTrue(motDePasseViolation);
    }
}
