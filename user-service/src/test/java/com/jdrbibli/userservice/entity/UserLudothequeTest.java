package com.jdrbibli.userservice.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserLudothequeTest {

    @Test
    void testGettersAndSetters() {
        UserLudotheque ul = new UserLudotheque();

        Long id = 1L;
        UserProfile user = new UserProfile();
        user.setId(10L);
        Ouvrage ouvrage = new Ouvrage();
        ouvrage.setId(20L);

        // Setters
        ul.setId(id);
        ul.setUser(user);
        ul.setOuvrage(ouvrage);

        // Getters + assertions
        assertEquals(id, ul.getId(), "L'id doit correspondre");
        assertEquals(user, ul.getUser(), "L'utilisateur doit correspondre");
        assertEquals(ouvrage, ul.getOuvrage(), "L'ouvrage doit correspondre");
    }
}
