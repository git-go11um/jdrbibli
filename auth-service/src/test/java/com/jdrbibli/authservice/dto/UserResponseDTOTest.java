package com.jdrbibli.authservice.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.HashSet;

public class UserResponseDTOTest {

    @Test
    void testConstructorAndGetters() {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        roles.add("ROLE_ADMIN");

        UserResponseDTO dto = new UserResponseDTO(1L, "pseudoTest", "email@test.com", roles);

        assertEquals(1L, dto.getId());
        assertEquals("pseudoTest", dto.getPseudo());
        assertEquals("email@test.com", dto.getEmail());
        assertEquals(roles, dto.getRoles());
    }

    @Test
    void testSettersAndGetters() {
        UserResponseDTO dto = new UserResponseDTO();

        dto.setPseudo("pseudoSetter");
        dto.setEmail("setter@email.com");

        assertEquals("pseudoSetter", dto.getPseudo());
        assertEquals("setter@email.com", dto.getEmail());
    }
}
