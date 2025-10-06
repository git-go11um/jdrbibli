package com.jdrbibli.authservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncoderConfigTest {

    @Test
    void testPasswordEncoderBeanIsBCrypt() {
        PasswordEncoderConfig config = new PasswordEncoderConfig();

        PasswordEncoder encoder = config.passwordEncoder();

        assertNotNull(encoder, "PasswordEncoder ne doit pas être null");
        assertTrue(encoder instanceof org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder,
                "Doit être une instance de BCryptPasswordEncoder");
    }

    @Test
    void testPasswordEncodingAndMatching() {
        PasswordEncoderConfig config = new PasswordEncoderConfig();
        PasswordEncoder encoder = config.passwordEncoder();

        String rawPassword = "mySecret123";
        String encodedPassword = encoder.encode(rawPassword);

        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword, "Le mot de passe encodé ne doit pas être identique au brut");

        // Mot de passe correct
        assertTrue(encoder.matches(rawPassword, encodedPassword), "Le mot de passe doit correspondre");

        // Mauvais mot de passe
        assertFalse(encoder.matches("wrongPassword", encodedPassword),
                "Un mauvais mot de passe ne doit pas correspondre");
    }
}
