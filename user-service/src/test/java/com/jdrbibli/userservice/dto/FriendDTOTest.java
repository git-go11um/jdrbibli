package com.jdrbibli.userservice.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class FriendDTOTest {

    @Test
    void testGettersAndSetters() {
        FriendDTO friend = new FriendDTO();

        Long id = 10L;
        String pseudo = "friendUser";
        String email = "friend@example.com";
        String avatarUrl = "http://avatar.url/friend.png";

        friend.setId(id);
        friend.setPseudo(pseudo);
        friend.setEmail(email);
        friend.setAvatarUrl(avatarUrl);

        assertEquals(id, friend.getId());
        assertEquals(pseudo, friend.getPseudo());
        assertEquals(email, friend.getEmail());
        assertEquals(avatarUrl, friend.getAvatarUrl());
    }
}
