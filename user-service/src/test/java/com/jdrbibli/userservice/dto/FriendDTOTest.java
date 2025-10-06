package com.jdrbibli.userservice.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FriendDTOTest {

    @Test
    void testGettersAndSetters() {
        FriendDTO friend = new FriendDTO();

        // Valeurs de test
        Long id = 10L;
        String pseudo = "friendUser";
        String email = "friend@example.com";
        String avatarUrl = "http://avatar.url/friend.png";

        // Utilisation des setters
        friend.setId(id);
        friend.setPseudo(pseudo);
        friend.setEmail(email);
        friend.setAvatarUrl(avatarUrl);

        // Vérification des getters
        assertEquals(id, friend.getId(), "L'id doit correspondre");
        assertEquals(pseudo, friend.getPseudo(), "Le pseudo doit correspondre");
        assertEquals(email, friend.getEmail(), "L'email doit correspondre");
        assertEquals(avatarUrl, friend.getAvatarUrl(), "L'URL de l'avatar doit correspondre");
    }
}
