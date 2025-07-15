package com.jdrbibli.authservice.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ReponseProfileChangeTest {

    @Test
    void testConstructorAndGetter() {
        ReponseProfileChange response = new ReponseProfileChange("Modification réussie");
        assertEquals("Modification réussie", response.getMessage());
    }

    @Test
    void testSetter() {
        ReponseProfileChange response = new ReponseProfileChange();
        response.setMessage("Message mis à jour");
        assertEquals("Message mis à jour", response.getMessage());
    }
}
