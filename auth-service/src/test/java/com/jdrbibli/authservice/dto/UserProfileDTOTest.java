package com.jdrbibli.authservice.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserProfileDTOTest {

    @Test
    void testGettersAndSetters() {
        UserProfileDTO profile = new UserProfileDTO();

        profile.setId(1L);
        profile.setPseudo("userTest");
        profile.setEmail("user@test.com");

        assertThat(profile.getId()).isEqualTo(1L);
        assertThat(profile.getPseudo()).isEqualTo("userTest");
        assertThat(profile.getEmail()).isEqualTo("user@test.com");
    }

    @Test
    void testAllArgsConstructor() {
        UserProfileDTO profile = new UserProfileDTO(2L, "pseudo2", "email2@test.com");

        assertThat(profile.getId()).isEqualTo(2L);
        assertThat(profile.getPseudo()).isEqualTo("pseudo2");
        assertThat(profile.getEmail()).isEqualTo("email2@test.com");
    }

    @Test
    void testConstructorWithoutId() {
        UserProfileDTO profile = new UserProfileDTO("pseudo3", "email3@test.com");

        assertThat(profile.getId()).isNull();
        assertThat(profile.getPseudo()).isEqualTo("pseudo3");
        assertThat(profile.getEmail()).isEqualTo("email3@test.com");
    }
}
