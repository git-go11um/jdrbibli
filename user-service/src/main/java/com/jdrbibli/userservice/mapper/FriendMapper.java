package com.jdrbibli.userservice.mapper;

import com.jdrbibli.userservice.dto.FriendDTO;
import com.jdrbibli.userservice.entity.User;

public class FriendMapper {
    public static FriendDTO toDTO(User user) {
        if (user == null) return null;
        
        FriendDTO dto = new FriendDTO();
        dto.setId(user.getId());
        dto.setPseudo(user.getPseudo());
        dto.setEmail(user.getEmail());
        dto.setAvatarUrl(user.getAvatarUrl());
        return dto;
    }
}
