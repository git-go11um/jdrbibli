package com.jdrbibli.userservice.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserProfileDTOTest {

    @Test
    void testGettersAndSetters() {
        UserProfileDTO user = new UserProfileDTO();

        Long id = 1L;
        String pseudo = "Alice";
        String email = "alice@example.com";
        String avatarUrl = "http://avatar.url/alice.png";

        // Setters
        user.setId(id);
        user.setPseudo(pseudo);
        user.setEmail(email);
        user.setAvatarUrl(avatarUrl);

        // Getters + assertions
        assertEquals(id, user.getId(), "L'id doit correspondre");
        assertEquals(pseudo, user.getPseudo(), "Le pseudo doit correspondre");
        assertEquals(email, user.getEmail(), "L'email doit correspondre");
        assertEquals(avatarUrl, user.getAvatarUrl(), "L'URL de l'avatar doit correspondre");
    }

    @Test
    void testConstructors() {
        Long id = 1L;
        String pseudo = "Bob";
        String email = "bob@example.com";
        String avatarUrl = "http://avatar.url/bob.png";

        // Constructeur id, pseudo, email
        UserProfileDTO user1 = new UserProfileDTO(id, pseudo, email);
        assertEquals(id, user1.getId());
        assertEquals(pseudo, user1.getPseudo());
        assertEquals(email, user1.getEmail());
        assertNull(user1.getAvatarUrl());

        // Constructeur pseudo, email, avatarUrl
        UserProfileDTO user2 = new UserProfileDTO(pseudo, email, avatarUrl);
        assertNull(user2.getId());
        assertEquals(pseudo, user2.getPseudo());
        assertEquals(email, user2.getEmail());
        assertEquals(avatarUrl, user2.getAvatarUrl());

        // Constructeur complet
        UserProfileDTO user3 = new UserProfileDTO(id, pseudo, email, avatarUrl);
        assertEquals(id, user3.getId());
        assertEquals(pseudo, user3.getPseudo());
        assertEquals(email, user3.getEmail());
        assertEquals(avatarUrl, user3.getAvatarUrl());
    }
}
