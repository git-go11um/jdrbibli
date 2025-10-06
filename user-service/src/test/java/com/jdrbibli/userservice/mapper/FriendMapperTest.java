package com.jdrbibli.userservice.mapper;

import com.jdrbibli.userservice.dto.FriendDTO;
import com.jdrbibli.userservice.entity.UserProfile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FriendMapperTest {

    @Test
    void toDTO_shouldMapAllFields() {
        UserProfile user = new UserProfile();
        user.setId(1L);
        user.setPseudo("Alice");
        user.setEmail("alice@example.com");
        user.setAvatarUrl("http://avatar.url/alice.png");

        FriendDTO dto = FriendMapper.toDTO(user);

        assertNotNull(dto);
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getPseudo(), dto.getPseudo());
        assertEquals(user.getEmail(), dto.getEmail());
        assertEquals(user.getAvatarUrl(), dto.getAvatarUrl());
    }

    @Test
    void toDTO_shouldReturnNullIfUserIsNull() {
        FriendDTO dto = FriendMapper.toDTO(null);
        assertNull(dto, "Le DTO doit être null si l'utilisateur est null");
    }
}
