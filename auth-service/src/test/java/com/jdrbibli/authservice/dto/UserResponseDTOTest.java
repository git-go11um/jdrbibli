package com.jdrbibli.authservice.dto;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserResponseDTOTest {

    @Test
    void testAllArgsConstructor() {
        Set<String> roles = Set.of("ROLE_USER", "ROLE_ADMIN");
        UserResponseDTO dto = new UserResponseDTO(1L, "pseudo1", "email@test.com", roles);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getPseudo()).isEqualTo("pseudo1");
        assertThat(dto.getEmail()).isEqualTo("email@test.com");
        assertThat(dto.getRoles()).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    void testDefaultConstructorAndSetters() {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setPseudo("pseudo2");
        dto.setEmail("email2@test.com");

        assertThat(dto.getId()).isNull();
        assertThat(dto.getPseudo()).isEqualTo("pseudo2");
        assertThat(dto.getEmail()).isEqualTo("email2@test.com");
        assertThat(dto.getRoles()).isNull();
    }
}
