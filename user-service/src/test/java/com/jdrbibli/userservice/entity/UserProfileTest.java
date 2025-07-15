package com.jdrbibli.userservice.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.jdrbibli.userservice.dto.OuvrageDTO;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;

class UserProfileTest {

    @Test
    void testGettersAndSetters() {
        UserProfile user = new UserProfile();

        Long id = 42L;
        String pseudo = "user123";
        String email = "user@example.com";
        String avatarUrl = "http://avatar.url/image.png";
        List<Long> ouvrageIds = List.of(1L, 2L, 3L);

        OuvrageDTO ouvrage1 = new OuvrageDTO();
        ouvrage1.setId(1L);
        OuvrageDTO ouvrage2 = new OuvrageDTO();
        ouvrage2.setId(2L);
        List<OuvrageDTO> ludotheque = new ArrayList<>();
        ludotheque.add(ouvrage1);
        ludotheque.add(ouvrage2);

        user.setId(id);
        user.setPseudo(pseudo);
        user.setEmail(email);
        user.setAvatarUrl(avatarUrl);
        user.setOuvrageIds(ouvrageIds);
        user.setLudotheque(ludotheque);

        assertEquals(id, user.getId());
        assertEquals(pseudo, user.getPseudo());
        assertEquals(email, user.getEmail());
        assertEquals(avatarUrl, user.getAvatarUrl());
        assertEquals(ouvrageIds, user.getOuvrageIds());
        assertEquals(ludotheque, user.getLudotheque());
        assertEquals(2, user.getLudotheque().size());
        assertEquals(1L, user.getLudotheque().get(0).getId());
    }
}
