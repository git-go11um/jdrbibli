package com.jdrbibli.userservice.entity;

import com.jdrbibli.userservice.dto.OuvrageDTO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileTest {

    @Test
    void testGettersAndSetters() {
        UserProfile user = new UserProfile();

        Long id = 1L;
        String pseudo = "Alice";
        String email = "alice@example.com";
        String avatarUrl = "http://avatar.url/alice.png";
        String avatarPath = "/avatars/alice.png";

        // Ludotheque
        List<Long> ouvrageIds = new ArrayList<>();
        ouvrageIds.add(101L);
        ouvrageIds.add(102L);

        List<OuvrageDTO> ludotheque = new ArrayList<>();
        OuvrageDTO o1 = new OuvrageDTO();
        o1.setId(101L);
        o1.setTitre("Ouvrage 1");
        ludotheque.add(o1);

        // Friends
        List<UserProfile> friends = new ArrayList<>();
        UserProfile friend = new UserProfile();
        friend.setId(2L);
        friends.add(friend);

        // Gammes
        Set<Gamme> gammes = new HashSet<>();
        Gamme g = new Gamme();
        g.setId(10L);
        gammes.add(g);

        // Setters
        user.setId(id);
        user.setPseudo(pseudo);
        user.setEmail(email);
        user.setAvatarUrl(avatarUrl);
        user.setAvatarPath(avatarPath);
        user.setOuvrageIds(ouvrageIds);
        user.setLudotheque(ludotheque);
        user.setFriends(friends);
        user.setGammes(gammes);

        // Getters + assertions
        assertEquals(id, user.getId());
        assertEquals(pseudo, user.getPseudo());
        assertEquals(email, user.getEmail());
        assertEquals(avatarUrl, user.getAvatarUrl());
        assertEquals(avatarPath, user.getAvatarPath());
        assertEquals(ouvrageIds, user.getOuvrageIds());
        assertEquals(ludotheque, user.getLudotheque());
        assertEquals(friends, user.getFriends());
        assertEquals(gammes, user.getGammes());
    }
}
